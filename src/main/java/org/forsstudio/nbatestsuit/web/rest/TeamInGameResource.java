package org.forsstudio.nbatestsuit.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.forsstudio.nbatestsuit.repository.TeamInGameRepository;
import org.forsstudio.nbatestsuit.service.TeamInGameService;
import org.forsstudio.nbatestsuit.service.dto.TeamInGameDTO;
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
 * REST controller for managing {@link org.forsstudio.nbatestsuit.domain.TeamInGame}.
 */
@RestController
@RequestMapping("/api/team-in-games")
public class TeamInGameResource {

    private final Logger log = LoggerFactory.getLogger(TeamInGameResource.class);

    private static final String ENTITY_NAME = "teamInGame";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TeamInGameService teamInGameService;

    private final TeamInGameRepository teamInGameRepository;

    public TeamInGameResource(TeamInGameService teamInGameService, TeamInGameRepository teamInGameRepository) {
        this.teamInGameService = teamInGameService;
        this.teamInGameRepository = teamInGameRepository;
    }

    /**
     * {@code POST  /team-in-games} : Create a new teamInGame.
     *
     * @param teamInGameDTO the teamInGameDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teamInGameDTO, or with status {@code 400 (Bad Request)} if the teamInGame has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TeamInGameDTO>> createTeamInGame(@RequestBody TeamInGameDTO teamInGameDTO) throws URISyntaxException {
        log.debug("REST request to save TeamInGame : {}", teamInGameDTO);
        if (teamInGameDTO.getId() != null) {
            throw new BadRequestAlertException("A new teamInGame cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return teamInGameService
            .save(teamInGameDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/team-in-games/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /team-in-games/:id} : Updates an existing teamInGame.
     *
     * @param id the id of the teamInGameDTO to save.
     * @param teamInGameDTO the teamInGameDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamInGameDTO,
     * or with status {@code 400 (Bad Request)} if the teamInGameDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the teamInGameDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TeamInGameDTO>> updateTeamInGame(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TeamInGameDTO teamInGameDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TeamInGame : {}, {}", id, teamInGameDTO);
        if (teamInGameDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teamInGameDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return teamInGameRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return teamInGameService
                    .update(teamInGameDTO)
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
     * {@code PATCH  /team-in-games/:id} : Partial updates given fields of an existing teamInGame, field will ignore if it is null
     *
     * @param id the id of the teamInGameDTO to save.
     * @param teamInGameDTO the teamInGameDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamInGameDTO,
     * or with status {@code 400 (Bad Request)} if the teamInGameDTO is not valid,
     * or with status {@code 404 (Not Found)} if the teamInGameDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the teamInGameDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TeamInGameDTO>> partialUpdateTeamInGame(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TeamInGameDTO teamInGameDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TeamInGame partially : {}, {}", id, teamInGameDTO);
        if (teamInGameDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teamInGameDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return teamInGameRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TeamInGameDTO> result = teamInGameService.partialUpdate(teamInGameDTO);

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
     * {@code GET  /team-in-games} : get all the teamInGames.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of teamInGames in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TeamInGameDTO>>> getAllTeamInGames(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of TeamInGames");
        return teamInGameService
            .countAll()
            .zipWith(teamInGameService.findAll(pageable).collectList())
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
     * {@code GET  /team-in-games/:id} : get the "id" teamInGame.
     *
     * @param id the id of the teamInGameDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teamInGameDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TeamInGameDTO>> getTeamInGame(@PathVariable("id") Long id) {
        log.debug("REST request to get TeamInGame : {}", id);
        Mono<TeamInGameDTO> teamInGameDTO = teamInGameService.findOne(id);
        return ResponseUtil.wrapOrNotFound(teamInGameDTO);
    }

    /**
     * {@code DELETE  /team-in-games/:id} : delete the "id" teamInGame.
     *
     * @param id the id of the teamInGameDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTeamInGame(@PathVariable("id") Long id) {
        log.debug("REST request to delete TeamInGame : {}", id);
        return teamInGameService
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
     * {@code SEARCH  /team-in-games/_search?query=:query} : search for the teamInGame corresponding
     * to the query.
     *
     * @param query the query of the teamInGame search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<TeamInGameDTO>>> searchTeamInGames(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of TeamInGames for query {}", query);
        return teamInGameService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(teamInGameService.search(query, pageable)));
    }
}
