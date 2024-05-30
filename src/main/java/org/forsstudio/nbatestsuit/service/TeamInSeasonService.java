package org.forsstudio.nbatestsuit.service;

import org.forsstudio.nbatestsuit.service.dto.TeamInSeasonDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link org.forsstudio.nbatestsuit.domain.TeamInSeason}.
 */
public interface TeamInSeasonService {
    /**
     * Save a teamInSeason.
     *
     * @param teamInSeasonDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TeamInSeasonDTO> save(TeamInSeasonDTO teamInSeasonDTO);

    /**
     * Updates a teamInSeason.
     *
     * @param teamInSeasonDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TeamInSeasonDTO> update(TeamInSeasonDTO teamInSeasonDTO);

    /**
     * Partially updates a teamInSeason.
     *
     * @param teamInSeasonDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TeamInSeasonDTO> partialUpdate(TeamInSeasonDTO teamInSeasonDTO);

    /**
     * Get all the teamInSeasons.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TeamInSeasonDTO> findAll(Pageable pageable);

    /**
     * Returns the number of teamInSeasons available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of teamInSeasons available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" teamInSeason.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TeamInSeasonDTO> findOne(Long id);

    /**
     * Delete the "id" teamInSeason.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the teamInSeason corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TeamInSeasonDTO> search(String query, Pageable pageable);
}
