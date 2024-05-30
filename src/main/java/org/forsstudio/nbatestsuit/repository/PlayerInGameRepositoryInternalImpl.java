package org.forsstudio.nbatestsuit.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.forsstudio.nbatestsuit.domain.PlayerInGame;
import org.forsstudio.nbatestsuit.repository.rowmapper.GameRowMapper;
import org.forsstudio.nbatestsuit.repository.rowmapper.PlayerInGameRowMapper;
import org.forsstudio.nbatestsuit.repository.rowmapper.PlayerRowMapper;
import org.forsstudio.nbatestsuit.repository.rowmapper.TeamInGameRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the PlayerInGame entity.
 */
@SuppressWarnings("unused")
class PlayerInGameRepositoryInternalImpl extends SimpleR2dbcRepository<PlayerInGame, Long> implements PlayerInGameRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TeamInGameRowMapper teamingameMapper;
    private final PlayerRowMapper playerMapper;
    private final GameRowMapper gameMapper;
    private final PlayerInGameRowMapper playeringameMapper;

    private static final Table entityTable = Table.aliased("player_in_game", EntityManager.ENTITY_ALIAS);
    private static final Table teamTable = Table.aliased("team_in_game", "team");
    private static final Table playerTable = Table.aliased("player", "player");
    private static final Table gameTable = Table.aliased("game", "game");

    public PlayerInGameRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TeamInGameRowMapper teamingameMapper,
        PlayerRowMapper playerMapper,
        GameRowMapper gameMapper,
        PlayerInGameRowMapper playeringameMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(PlayerInGame.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.teamingameMapper = teamingameMapper;
        this.playerMapper = playerMapper;
        this.gameMapper = gameMapper;
        this.playeringameMapper = playeringameMapper;
    }

    @Override
    public Flux<PlayerInGame> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<PlayerInGame> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PlayerInGameSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TeamInGameSqlHelper.getColumns(teamTable, "team"));
        columns.addAll(PlayerSqlHelper.getColumns(playerTable, "player"));
        columns.addAll(GameSqlHelper.getColumns(gameTable, "game"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(teamTable)
            .on(Column.create("team_id", entityTable))
            .equals(Column.create("id", teamTable))
            .leftOuterJoin(playerTable)
            .on(Column.create("player_id", entityTable))
            .equals(Column.create("id", playerTable))
            .leftOuterJoin(gameTable)
            .on(Column.create("game_id", entityTable))
            .equals(Column.create("id", gameTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, PlayerInGame.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<PlayerInGame> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<PlayerInGame> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private PlayerInGame process(Row row, RowMetadata metadata) {
        PlayerInGame entity = playeringameMapper.apply(row, "e");
        entity.setTeam(teamingameMapper.apply(row, "team"));
        entity.setPlayer(playerMapper.apply(row, "player"));
        entity.setGame(gameMapper.apply(row, "game"));
        return entity;
    }

    @Override
    public <S extends PlayerInGame> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
