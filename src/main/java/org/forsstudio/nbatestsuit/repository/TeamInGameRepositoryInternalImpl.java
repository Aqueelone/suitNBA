package org.forsstudio.nbatestsuit.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.forsstudio.nbatestsuit.domain.TeamInGame;
import org.forsstudio.nbatestsuit.repository.rowmapper.GameRowMapper;
import org.forsstudio.nbatestsuit.repository.rowmapper.TeamInGameRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the TeamInGame entity.
 */
@SuppressWarnings("unused")
class TeamInGameRepositoryInternalImpl extends SimpleR2dbcRepository<TeamInGame, Long> implements TeamInGameRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TeamInSeasonRowMapper teaminseasonMapper;
    private final GameRowMapper gameMapper;
    private final TeamInGameRowMapper teamingameMapper;

    private static final Table entityTable = Table.aliased("team_in_game", EntityManager.ENTITY_ALIAS);
    private static final Table teamTable = Table.aliased("team_in_season", "team");
    private static final Table gameTable = Table.aliased("game", "game");

    public TeamInGameRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TeamInSeasonRowMapper teaminseasonMapper,
        GameRowMapper gameMapper,
        TeamInGameRowMapper teamingameMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TeamInGame.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.teaminseasonMapper = teaminseasonMapper;
        this.gameMapper = gameMapper;
        this.teamingameMapper = teamingameMapper;
    }

    @Override
    public Flux<TeamInGame> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TeamInGame> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TeamInGameSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TeamInSeasonSqlHelper.getColumns(teamTable, "team"));
        columns.addAll(GameSqlHelper.getColumns(gameTable, "game"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(teamTable)
            .on(Column.create("team_id", entityTable))
            .equals(Column.create("id", teamTable))
            .leftOuterJoin(gameTable)
            .on(Column.create("game_id", entityTable))
            .equals(Column.create("id", gameTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TeamInGame.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TeamInGame> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TeamInGame> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private TeamInGame process(Row row, RowMetadata metadata) {
        TeamInGame entity = teamingameMapper.apply(row, "e");
        entity.setTeam(teaminseasonMapper.apply(row, "team"));
        entity.setGame(gameMapper.apply(row, "game"));
        return entity;
    }

    @Override
    public <S extends TeamInGame> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
