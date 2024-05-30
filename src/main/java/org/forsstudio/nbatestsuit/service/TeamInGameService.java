package org.forsstudio.nbatestsuit.service;

import org.forsstudio.nbatestsuit.service.dto.TeamInGameDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link org.forsstudio.nbatestsuit.domain.TeamInGame}.
 */
public interface TeamInGameService {
    /**
     * Save a teamInGame.
     *
     * @param teamInGameDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TeamInGameDTO> save(TeamInGameDTO teamInGameDTO);

    /**
     * Updates a teamInGame.
     *
     * @param teamInGameDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TeamInGameDTO> update(TeamInGameDTO teamInGameDTO);

    /**
     * Partially updates a teamInGame.
     *
     * @param teamInGameDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TeamInGameDTO> partialUpdate(TeamInGameDTO teamInGameDTO);

    /**
     * Get all the teamInGames.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TeamInGameDTO> findAll(Pageable pageable);

    /**
     * Returns the number of teamInGames available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of teamInGames available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" teamInGame.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TeamInGameDTO> findOne(Long id);

    /**
     * Delete the "id" teamInGame.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the teamInGame corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TeamInGameDTO> search(String query, Pageable pageable);
}
