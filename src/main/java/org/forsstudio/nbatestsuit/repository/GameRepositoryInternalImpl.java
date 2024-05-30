package org.forsstudio.nbatestsuit.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.forsstudio.nbatestsuit.domain.Game;
import org.forsstudio.nbatestsuit.repository.rowmapper.GameRowMapper;
import org.forsstudio.nbatestsuit.repository.rowmapper.SeasonRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Game entity.
 */
@SuppressWarnings("unused")
class GameRepositoryInternalImpl extends SimpleR2dbcRepository<Game, Long> implements GameRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final SeasonRowMapper seasonMapper;
    private final GameRowMapper gameMapper;

    private static final Table entityTable = Table.aliased("game", EntityManager.ENTITY_ALIAS);
    private static final Table seasonTable = Table.aliased("season", "season");

    public GameRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        SeasonRowMapper seasonMapper,
        GameRowMapper gameMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Game.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.seasonMapper = seasonMapper;
        this.gameMapper = gameMapper;
    }

    @Override
    public Flux<Game> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Game> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = GameSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(SeasonSqlHelper.getColumns(seasonTable, "season"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(seasonTable)
            .on(Column.create("season_id", entityTable))
            .equals(Column.create("id", seasonTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Game.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Game> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Game> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Game process(Row row, RowMetadata metadata) {
        Game entity = gameMapper.apply(row, "e");
        entity.setSeason(seasonMapper.apply(row, "season"));
        return entity;
    }

    @Override
    public <S extends Game> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
