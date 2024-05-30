package org.forsstudio.nbatestsuit.repository;

import org.forsstudio.nbatestsuit.domain.PlayerInGame;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the PlayerInGame entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlayerInGameRepository extends ReactiveCrudRepository<PlayerInGame, Long>, PlayerInGameRepositoryInternal {
    Flux<PlayerInGame> findAllBy(Pageable pageable);

    @Query("SELECT * FROM player_in_game entity WHERE entity.team_id = :id")
    Flux<PlayerInGame> findByTeam(Long id);

    @Query("SELECT * FROM player_in_game entity WHERE entity.team_id IS NULL")
    Flux<PlayerInGame> findAllWhereTeamIsNull();

    @Query("SELECT * FROM player_in_game entity WHERE entity.player_id = :id")
    Flux<PlayerInGame> findByPlayer(Long id);

    @Query("SELECT * FROM player_in_game entity WHERE entity.player_id IS NULL")
    Flux<PlayerInGame> findAllWherePlayerIsNull();

    @Query("SELECT * FROM player_in_game entity WHERE entity.game_id = :id")
    Flux<PlayerInGame> findByGame(Long id);

    @Query("SELECT * FROM player_in_game entity WHERE entity.game_id IS NULL")
    Flux<PlayerInGame> findAllWhereGameIsNull();

    @Override
    <S extends PlayerInGame> Mono<S> save(S entity);

    @Override
    Flux<PlayerInGame> findAll();

    @Override
    Mono<PlayerInGame> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PlayerInGameRepositoryInternal {
    <S extends PlayerInGame> Mono<S> save(S entity);

    Flux<PlayerInGame> findAllBy(Pageable pageable);

    Flux<PlayerInGame> findAll();

    Mono<PlayerInGame> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<PlayerInGame> findAllBy(Pageable pageable, Criteria criteria);
}
