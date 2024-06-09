package org.forsstudio.nbatestsuit.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.forsstudio.nbatestsuit.domain.PlayerInTeam;
import org.forsstudio.nbatestsuit.repository.rowmapper.PlayerInTeamRowMapper;
import org.forsstudio.nbatestsuit.repository.rowmapper.PlayerRowMapper;
import org.forsstudio.nbatestsuit.repository.rowmapper.TeamInSeasonRowMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the PlayerInTeam entity.
 */
@SuppressWarnings("unused")
class PlayerInTeamRepositoryInternalImpl extends SimpleR2dbcRepository<PlayerInTeam, Long> implements PlayerInTeamRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PlayerRowMapper playerMapper;
    private final TeamInSeasonRowMapper teaminseasonMapper;
    private final PlayerInTeamRowMapper playerinteamMapper;

    private static final Table entityTable = Table.aliased("player_in_team", EntityManager.ENTITY_ALIAS);
    private static final Table playerTable = Table.aliased("player", "player");
    private static final Table teamInSeasonTable = Table.aliased("team_in_season", "teamInSeason");

    public PlayerInTeamRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PlayerRowMapper playerMapper,
        TeamInSeasonRowMapper teaminseasonMapper,
        PlayerInTeamRowMapper playerinteamMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(PlayerInTeam.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.playerMapper = playerMapper;
        this.teaminseasonMapper = teaminseasonMapper;
        this.playerinteamMapper = playerinteamMapper;
    }

    @Override
    public Flux<PlayerInTeam> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<PlayerInTeam> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PlayerInTeamSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PlayerSqlHelper.getColumns(playerTable, "player"));
        columns.addAll(TeamInSeasonSqlHelper.getColumns(teamInSeasonTable, "teamInSeason"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(playerTable)
            .on(Column.create("player_id", entityTable))
            .equals(Column.create("id", playerTable))
            .leftOuterJoin(teamInSeasonTable)
            .on(Column.create("team_in_season_id", entityTable))
            .equals(Column.create("id", teamInSeasonTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, PlayerInTeam.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<PlayerInTeam> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<PlayerInTeam> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private PlayerInTeam process(Row row, RowMetadata metadata) {
        PlayerInTeam entity = playerinteamMapper.apply(row, "e");
        entity.setPlayer(playerMapper.apply(row, "player"));
        entity.setTeamInSeason(teaminseasonMapper.apply(row, "teamInSeason"));
        return entity;
    }

    @Override
    public <S extends PlayerInTeam> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
