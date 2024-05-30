package org.forsstudio.nbatestsuit.service;

import org.forsstudio.nbatestsuit.service.dto.SeasonDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link org.forsstudio.nbatestsuit.domain.Season}.
 */
public interface SeasonService {
    /**
     * Save a season.
     *
     * @param seasonDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<SeasonDTO> save(SeasonDTO seasonDTO);

    /**
     * Updates a season.
     *
     * @param seasonDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<SeasonDTO> update(SeasonDTO seasonDTO);

    /**
     * Partially updates a season.
     *
     * @param seasonDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<SeasonDTO> partialUpdate(SeasonDTO seasonDTO);

    /**
     * Get all the seasons.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<SeasonDTO> findAll(Pageable pageable);

    /**
     * Returns the number of seasons available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of seasons available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" season.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<SeasonDTO> findOne(Long id);

    /**
     * Delete the "id" season.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the season corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<SeasonDTO> search(String query, Pageable pageable);
}
