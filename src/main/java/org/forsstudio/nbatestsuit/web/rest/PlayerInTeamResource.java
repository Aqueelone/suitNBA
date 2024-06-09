package org.forsstudio.nbatestsuit.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.forsstudio.nbatestsuit.repository.PlayerInTeamRepository;
import org.forsstudio.nbatestsuit.service.PlayerInTeamService;
import org.forsstudio.nbatestsuit.service.dto.PlayerInTeamDTO;
import org.forsstudio.nbatestsuit.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link org.forsstudio.nbatestsuit.domain.PlayerInTeam}.
 */
@RestController
@RequestMapping("/api/player-in-teams")
public class PlayerInTeamResource {

    private final Logger log = LoggerFactory.getLogger(PlayerInTeamResource.class);

    private static final String ENTITY_NAME = "playerInTeam";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlayerInTeamService playerInTeamService;

    private final PlayerInTeamRepository playerInTeamRepository;

    public PlayerInTeamResource(PlayerInTeamService playerInTeamService, PlayerInTeamRepository playerInTeamRepository) {
        this.playerInTeamService = playerInTeamService;
        this.playerInTeamRepository = playerInTeamRepository;
    }

    /**
     * {@code POST  /player-in-teams} : Create a new playerInTeam.
     *
     * @param playerInTeamDTO the playerInTeamDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new playerInTeamDTO, or with status {@code 400 (Bad Request)} if the playerInTeam has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<PlayerInTeamDTO>> createPlayerInTeam(@RequestBody PlayerInTeamDTO playerInTeamDTO)
        throws URISyntaxException {
        log.debug("REST request to save PlayerInTeam : {}", playerInTeamDTO);
        if (playerInTeamDTO.getId() != null) {
            throw new BadRequestAlertException("A new playerInTeam cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return playerInTeamService
            .save(playerInTeamDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/player-in-teams/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /player-in-teams/:id} : Updates an existing playerInTeam.
     *
     * @param id the id of the playerInTeamDTO to save.
     * @param playerInTeamDTO the playerInTeamDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated playerInTeamDTO,
     * or with status {@code 400 (Bad Request)} if the playerInTeamDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the playerInTeamDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<PlayerInTeamDTO>> updatePlayerInTeam(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PlayerInTeamDTO playerInTeamDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PlayerInTeam : {}, {}", id, playerInTeamDTO);
        if (playerInTeamDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, playerInTeamDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return playerInTeamRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return playerInTeamService
                    .update(playerInTeamDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(
                        result ->
                            ResponseEntity.ok()
                                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                                .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /player-in-teams/:id} : Partial updates given fields of an existing playerInTeam, field will ignore if it is null
     *
     * @param id the id of the playerInTeamDTO to save.
     * @param playerInTeamDTO the playerInTeamDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated playerInTeamDTO,
     * or with status {@code 400 (Bad Request)} if the playerInTeamDTO is not valid,
     * or with status {@code 404 (Not Found)} if the playerInTeamDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the playerInTeamDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<PlayerInTeamDTO>> partialUpdatePlayerInTeam(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PlayerInTeamDTO playerInTeamDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PlayerInTeam partially : {}, {}", id, playerInTeamDTO);
        if (playerInTeamDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, playerInTeamDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return playerInTeamRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<PlayerInTeamDTO> result = playerInTeamService.partialUpdate(playerInTeamDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(
                        res ->
                            ResponseEntity.ok()
                                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                                .body(res)
                    );
            });
    }

    /**
     * {@code GET  /player-in-teams} : get all the playerInTeams.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of playerInTeams in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<PlayerInTeamDTO>>> getAllPlayerInTeams(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of PlayerInTeams");
        return playerInTeamService
            .countAll()
            .zipWith(playerInTeamService.findAll(pageable).collectList())
            .map(
                countWithEntities ->
                    ResponseEntity.ok()
                        .headers(
                            PaginationUtil.generatePaginationHttpHeaders(
                                ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                                new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                            )
                        )
                        .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /player-in-teams/:id} : get the "id" playerInTeam.
     *
     * @param id the id of the playerInTeamDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the playerInTeamDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<PlayerInTeamDTO>> getPlayerInTeam(@PathVariable("id") Long id) {
        log.debug("REST request to get PlayerInTeam : {}", id);
        Mono<PlayerInTeamDTO> playerInTeamDTO = playerInTeamService.findOne(id);
        return ResponseUtil.wrapOrNotFound(playerInTeamDTO);
    }

    /**
     * {@code DELETE  /player-in-teams/:id} : delete the "id" playerInTeam.
     *
     * @param id the id of the playerInTeamDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePlayerInTeam(@PathVariable("id") Long id) {
        log.debug("REST request to delete PlayerInTeam : {}", id);
        return playerInTeamService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code SEARCH  /player-in-teams/_search?query=:query} : search for the playerInTeam corresponding
     * to the query.
     *
     * @param query the query of the playerInTeam search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<PlayerInTeamDTO>>> searchPlayerInTeams(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of PlayerInTeams for query {}", query);
        return playerInTeamService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(playerInTeamService.search(query, pageable)));
    }
}
