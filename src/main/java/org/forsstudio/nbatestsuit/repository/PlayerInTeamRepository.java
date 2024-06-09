package org.forsstudio.nbatestsuit.repository;

import org.forsstudio.nbatestsuit.domain.PlayerInTeam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the PlayerInTeam entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlayerInTeamRepository extends ReactiveCrudRepository<PlayerInTeam, Long>, PlayerInTeamRepositoryInternal {
    Flux<PlayerInTeam> findAllBy(Pageable pageable);

    @Query("SELECT * FROM player_in_team entity WHERE entity.player_id = :id")
    Flux<PlayerInTeam> findByPlayer(Long id);

    @Query("SELECT * FROM player_in_team entity WHERE entity.player_id IS NULL")
    Flux<PlayerInTeam> findAllWherePlayerIsNull();

    @Query("SELECT * FROM player_in_team entity WHERE entity.team_in_season_id = :id")
    Flux<PlayerInTeam> findByTeamInSeason(Long id);

    @Query("SELECT * FROM player_in_team entity WHERE entity.team_in_season_id IS NULL")
    Flux<PlayerInTeam> findAllWhereTeamInSeasonIsNull();

    @Override
    <S extends PlayerInTeam> Mono<S> save(S entity);

    @Override
    Flux<PlayerInTeam> findAll();

    @Override
    Mono<PlayerInTeam> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PlayerInTeamRepositoryInternal {
    <S extends PlayerInTeam> Mono<S> save(S entity);

    Flux<PlayerInTeam> findAllBy(Pageable pageable);

    Flux<PlayerInTeam> findAll();

    Mono<PlayerInTeam> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<PlayerInTeam> findAllBy(Pageable pageable, Criteria criteria);
}
