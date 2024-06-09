package org.forsstudio.nbatestsuit.service;

import org.forsstudio.nbatestsuit.service.dto.PlayerInTeamDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link org.forsstudio.nbatestsuit.domain.PlayerInTeam}.
 */
public interface PlayerInTeamService {
    /**
     * Save a playerInTeam.
     *
     * @param playerInTeamDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<PlayerInTeamDTO> save(PlayerInTeamDTO playerInTeamDTO);

    /**
     * Updates a playerInTeam.
     *
     * @param playerInTeamDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<PlayerInTeamDTO> update(PlayerInTeamDTO playerInTeamDTO);

    /**
     * Partially updates a playerInTeam.
     *
     * @param playerInTeamDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<PlayerInTeamDTO> partialUpdate(PlayerInTeamDTO playerInTeamDTO);

    /**
     * Get all the playerInTeams.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<PlayerInTeamDTO> findAll(Pageable pageable);

    /**
     * Returns the number of playerInTeams available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of playerInTeams available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" playerInTeam.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<PlayerInTeamDTO> findOne(Long id);

    /**
     * Delete the "id" playerInTeam.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the playerInTeam corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<PlayerInTeamDTO> search(String query, Pageable pageable);
}
