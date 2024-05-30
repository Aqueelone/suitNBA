package org.forsstudio.nbatestsuit.repository;

import org.forsstudio.nbatestsuit.domain.TeamInSeason;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TeamInSeason entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TeamInSeasonRepository extends ReactiveCrudRepository<TeamInSeason, Long>, TeamInSeasonRepositoryInternal {
    Flux<TeamInSeason> findAllBy(Pageable pageable);

    @Query("SELECT * FROM team_in_season entity WHERE entity.team_id = :id")
    Flux<TeamInSeason> findByTeam(Long id);

    @Query("SELECT * FROM team_in_season entity WHERE entity.team_id IS NULL")
    Flux<TeamInSeason> findAllWhereTeamIsNull();

    @Query("SELECT * FROM team_in_season entity WHERE entity.season_id = :id")
    Flux<TeamInSeason> findBySeason(Long id);

    @Query("SELECT * FROM team_in_season entity WHERE entity.season_id IS NULL")
    Flux<TeamInSeason> findAllWhereSeasonIsNull();

    @Override
    <S extends TeamInSeason> Mono<S> save(S entity);

    @Override
    Flux<TeamInSeason> findAll();

    @Override
    Mono<TeamInSeason> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TeamInSeasonRepositoryInternal {
    <S extends TeamInSeason> Mono<S> save(S entity);

    Flux<TeamInSeason> findAllBy(Pageable pageable);

    Flux<TeamInSeason> findAll();

    Mono<TeamInSeason> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TeamInSeason> findAllBy(Pageable pageable, Criteria criteria);
}
