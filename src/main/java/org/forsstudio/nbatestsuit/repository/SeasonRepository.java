package org.forsstudio.nbatestsuit.repository;

import org.forsstudio.nbatestsuit.domain.Season;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Season entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SeasonRepository extends ReactiveCrudRepository<Season, Long>, SeasonRepositoryInternal {
    Flux<Season> findAllBy(Pageable pageable);

    @Override
    <S extends Season> Mono<S> save(S entity);

    @Override
    Flux<Season> findAll();

    @Override
    Mono<Season> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SeasonRepositoryInternal {
    <S extends Season> Mono<S> save(S entity);

    Flux<Season> findAllBy(Pageable pageable);

    Flux<Season> findAll();

    Mono<Season> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Season> findAllBy(Pageable pageable, Criteria criteria);
}
