package org.forsstudio.nbatestsuit.service;

import org.forsstudio.nbatestsuit.service.dto.PlayerInGameDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link org.forsstudio.nbatestsuit.domain.PlayerInGame}.
 */
public interface PlayerInGameService {
    /**
     * Save a playerInGame.
     *
     * @param playerInGameDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<PlayerInGameDTO> save(PlayerInGameDTO playerInGameDTO);

    /**
     * Updates a playerInGame.
     *
     * @param playerInGameDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<PlayerInGameDTO> update(PlayerInGameDTO playerInGameDTO);

    /**
     * Partially updates a playerInGame.
     *
     * @param playerInGameDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<PlayerInGameDTO> partialUpdate(PlayerInGameDTO playerInGameDTO);

    /**
     * Get all the playerInGames.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<PlayerInGameDTO> findAll(Pageable pageable);

    /**
     * Returns the number of playerInGames available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of playerInGames available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" playerInGame.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<PlayerInGameDTO> findOne(Long id);

    /**
     * Delete the "id" playerInGame.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the playerInGame corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<PlayerInGameDTO> search(String query, Pageable pageable);
}
