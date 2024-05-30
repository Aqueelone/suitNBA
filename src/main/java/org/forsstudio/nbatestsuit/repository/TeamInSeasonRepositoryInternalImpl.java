package org.forsstudio.nbatestsuit.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.forsstudio.nbatestsuit.domain.TeamInSeason;
import org.forsstudio.nbatestsuit.repository.rowmapper.SeasonRowMapper;
import org.forsstudio.nbatestsuit.repository.rowmapper.TeamInSeasonRowMapper;
import org.forsstudio.nbatestsuit.repository.rowmapper.TeamRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the TeamInSeason entity.
 */
@SuppressWarnings("unused")
class TeamInSeasonRepositoryInternalImpl extends SimpleR2dbcRepository<TeamInSeason, Long> implements TeamInSeasonRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TeamRowMapper teamMapper;
    private final SeasonRowMapper seasonMapper;
    private final TeamInSeasonRowMapper teaminseasonMapper;

    private static final Table entityTable = Table.aliased("team_in_season", EntityManager.ENTITY_ALIAS);
    private static final Table teamTable = Table.aliased("team", "team");
    private static final Table seasonTable = Table.aliased("season", "season");

    public TeamInSeasonRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TeamRowMapper teamMapper,
        SeasonRowMapper seasonMapper,
        TeamInSeasonRowMapper teaminseasonMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(TeamInSeason.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.teamMapper = teamMapper;
        this.seasonMapper = seasonMapper;
        this.teaminseasonMapper = teaminseasonMapper;
    }

    @Override
    public Flux<TeamInSeason> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<TeamInSeason> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TeamInSeasonSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TeamSqlHelper.getColumns(teamTable, "team"));
        columns.addAll(SeasonSqlHelper.getColumns(seasonTable, "season"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(teamTable)
            .on(Column.create("team_id", entityTable))
            .equals(Column.create("id", teamTable))
            .leftOuterJoin(seasonTable)
            .on(Column.create("season_id", entityTable))
            .equals(Column.create("id", seasonTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, TeamInSeason.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<TeamInSeason> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<TeamInSeason> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private TeamInSeason process(Row row, RowMetadata metadata) {
        TeamInSeason entity = teaminseasonMapper.apply(row, "e");
        entity.setTeam(teamMapper.apply(row, "team"));
        entity.setSeason(seasonMapper.apply(row, "season"));
        return entity;
    }

    @Override
    public <S extends TeamInSeason> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
