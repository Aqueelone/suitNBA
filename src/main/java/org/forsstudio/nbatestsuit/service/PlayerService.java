package org.forsstudio.nbatestsuit.service;

import org.forsstudio.nbatestsuit.service.dto.PlayerDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link org.forsstudio.nbatestsuit.domain.Player}.
 */
public interface PlayerService {
    /**
     * Save a player.
     *
     * @param playerDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<PlayerDTO> save(PlayerDTO playerDTO);

    /**
     * Updates a player.
     *
     * @param playerDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<PlayerDTO> update(PlayerDTO playerDTO);

    /**
     * Partially updates a player.
     *
     * @param playerDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<PlayerDTO> partialUpdate(PlayerDTO playerDTO);

    /**
     * Get all the players.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<PlayerDTO> findAll(Pageable pageable);

    /**
     * Returns the number of players available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of players available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" player.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<PlayerDTO> findOne(Long id);

    /**
     * Delete the "id" player.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the player corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<PlayerDTO> search(String query, Pageable pageable);
}
