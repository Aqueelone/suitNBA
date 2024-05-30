package org.forsstudio.nbatestsuit.service;

import org.forsstudio.nbatestsuit.service.dto.TeamDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link org.forsstudio.nbatestsuit.domain.Team}.
 */
public interface TeamService {
    /**
     * Save a team.
     *
     * @param teamDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TeamDTO> save(TeamDTO teamDTO);

    /**
     * Updates a team.
     *
     * @param teamDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TeamDTO> update(TeamDTO teamDTO);

    /**
     * Partially updates a team.
     *
     * @param teamDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TeamDTO> partialUpdate(TeamDTO teamDTO);

    /**
     * Get all the teams.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TeamDTO> findAll(Pageable pageable);

    /**
     * Returns the number of teams available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of teams available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" team.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TeamDTO> findOne(Long id);

    /**
     * Delete the "id" team.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the team corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TeamDTO> search(String query, Pageable pageable);
}
