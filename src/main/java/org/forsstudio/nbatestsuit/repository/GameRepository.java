package org.forsstudio.nbatestsuit.repository;

import org.forsstudio.nbatestsuit.domain.Game;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Game entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GameRepository extends ReactiveCrudRepository<Game, Long>, GameRepositoryInternal {
    Flux<Game> findAllBy(Pageable pageable);

    @Query("SELECT * FROM game entity WHERE entity.season_id = :id")
    Flux<Game> findBySeason(Long id);

    @Query("SELECT * FROM game entity WHERE entity.season_id IS NULL")
    Flux<Game> findAllWhereSeasonIsNull();

    @Override
    <S extends Game> Mono<S> save(S entity);

    @Override
    Flux<Game> findAll();

    @Override
    Mono<Game> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface GameRepositoryInternal {
    <S extends Game> Mono<S> save(S entity);

    Flux<Game> findAllBy(Pageable pageable);

    Flux<Game> findAll();

    Mono<Game> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Game> findAllBy(Pageable pageable, Criteria criteria);
}
