package org.forsstudio.nbatestsuit.repository;

import org.forsstudio.nbatestsuit.domain.Player;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Player entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlayerRepository extends ReactiveCrudRepository<Player, Long>, PlayerRepositoryInternal {
    Flux<Player> findAllBy(Pageable pageable);

    @Override
    <S extends Player> Mono<S> save(S entity);

    @Override
    Flux<Player> findAll();

    @Override
    Mono<Player> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PlayerRepositoryInternal {
    <S extends Player> Mono<S> save(S entity);

    Flux<Player> findAllBy(Pageable pageable);

    Flux<Player> findAll();

    Mono<Player> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Player> findAllBy(Pageable pageable, Criteria criteria);
}
