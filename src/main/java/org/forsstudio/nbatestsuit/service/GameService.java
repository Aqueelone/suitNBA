package org.forsstudio.nbatestsuit.service;

import org.forsstudio.nbatestsuit.service.dto.GameDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link org.forsstudio.nbatestsuit.domain.Game}.
 */
public interface GameService {
    /**
     * Save a game.
     *
     * @param gameDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<GameDTO> save(GameDTO gameDTO);

    /**
     * Updates a game.
     *
     * @param gameDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<GameDTO> update(GameDTO gameDTO);

    /**
     * Partially updates a game.
     *
     * @param gameDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<GameDTO> partialUpdate(GameDTO gameDTO);

    /**
     * Get all the games.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<GameDTO> findAll(Pageable pageable);

    /**
     * Returns the number of games available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of games available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" game.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<GameDTO> findOne(Long id);

    /**
     * Delete the "id" game.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the game corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<GameDTO> search(String query, Pageable pageable);
}
