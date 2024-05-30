package org.forsstudio.nbatestsuit.repository;

import org.forsstudio.nbatestsuit.domain.TeamInGame;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TeamInGame entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TeamInGameRepository extends ReactiveCrudRepository<TeamInGame, Long>, TeamInGameRepositoryInternal {
    Flux<TeamInGame> findAllBy(Pageable pageable);

    @Query("SELECT * FROM team_in_game entity WHERE entity.team_id = :id")
    Flux<TeamInGame> findByTeam(Long id);

    @Query("SELECT * FROM team_in_game entity WHERE entity.team_id IS NULL")
    Flux<TeamInGame> findAllWhereTeamIsNull();

    @Query("SELECT * FROM team_in_game entity WHERE entity.game_id = :id")
    Flux<TeamInGame> findByGame(Long id);

    @Query("SELECT * FROM team_in_game entity WHERE entity.game_id IS NULL")
    Flux<TeamInGame> findAllWhereGameIsNull();

    @Override
    <S extends TeamInGame> Mono<S> save(S entity);

    @Override
    Flux<TeamInGame> findAll();

    @Override
    Mono<TeamInGame> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TeamInGameRepositoryInternal {
    <S extends TeamInGame> Mono<S> save(S entity);

    Flux<TeamInGame> findAllBy(Pageable pageable);

    Flux<TeamInGame> findAll();

    Mono<TeamInGame> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TeamInGame> findAllBy(Pageable pageable, Criteria criteria);
}
