package org.forsstudio.nbatestsuit.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.forsstudio.nbatestsuit.domain.Player;
import org.forsstudio.nbatestsuit.repository.rowmapper.PlayerRowMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Player entity.
 */
@SuppressWarnings("unused")
class PlayerRepositoryInternalImpl extends SimpleR2dbcRepository<Player, Long> implements PlayerRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PlayerRowMapper playerMapper;

    private static final Table entityTable = Table.aliased("player", EntityManager.ENTITY_ALIAS);

    public PlayerRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PlayerRowMapper playerMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Player.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.playerMapper = playerMapper;
    }

    @Override
    public Flux<Player> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Player> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PlayerSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Player.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Player> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Player> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Player process(Row row, RowMetadata metadata) {
        Player entity = playerMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Player> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
