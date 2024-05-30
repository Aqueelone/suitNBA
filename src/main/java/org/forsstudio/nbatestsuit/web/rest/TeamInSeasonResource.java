package org.forsstudio.nbatestsuit.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.forsstudio.nbatestsuit.repository.TeamInSeasonRepository;
import org.forsstudio.nbatestsuit.service.TeamInSeasonService;
import org.forsstudio.nbatestsuit.service.dto.TeamInSeasonDTO;
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
 * REST controller for managing {@link org.forsstudio.nbatestsuit.domain.TeamInSeason}.
 */
@RestController
@RequestMapping("/api/team-in-seasons")
public class TeamInSeasonResource {

    private final Logger log = LoggerFactory.getLogger(TeamInSeasonResource.class);

    private static final String ENTITY_NAME = "teamInSeason";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TeamInSeasonService teamInSeasonService;

    private final TeamInSeasonRepository teamInSeasonRepository;

    public TeamInSeasonResource(TeamInSeasonService teamInSeasonService, TeamInSeasonRepository teamInSeasonRepository) {
        this.teamInSeasonService = teamInSeasonService;
        this.teamInSeasonRepository = teamInSeasonRepository;
    }

    /**
     * {@code POST  /team-in-seasons} : Create a new teamInSeason.
     *
     * @param teamInSeasonDTO the teamInSeasonDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teamInSeasonDTO, or with status {@code 400 (Bad Request)} if the teamInSeason has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TeamInSeasonDTO>> createTeamInSeason(@RequestBody TeamInSeasonDTO teamInSeasonDTO)
        throws URISyntaxException {
        log.debug("REST request to save TeamInSeason : {}", teamInSeasonDTO);
        if (teamInSeasonDTO.getId() != null) {
            throw new BadRequestAlertException("A new teamInSeason cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return teamInSeasonService
            .save(teamInSeasonDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/team-in-seasons/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /team-in-seasons/:id} : Updates an existing teamInSeason.
     *
     * @param id the id of the teamInSeasonDTO to save.
     * @param teamInSeasonDTO the teamInSeasonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamInSeasonDTO,
     * or with status {@code 400 (Bad Request)} if the teamInSeasonDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the teamInSeasonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TeamInSeasonDTO>> updateTeamInSeason(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TeamInSeasonDTO teamInSeasonDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TeamInSeason : {}, {}", id, teamInSeasonDTO);
        if (teamInSeasonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teamInSeasonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return teamInSeasonRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return teamInSeasonService
                    .update(teamInSeasonDTO)
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
     * {@code PATCH  /team-in-seasons/:id} : Partial updates given fields of an existing teamInSeason, field will ignore if it is null
     *
     * @param id the id of the teamInSeasonDTO to save.
     * @param teamInSeasonDTO the teamInSeasonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamInSeasonDTO,
     * or with status {@code 400 (Bad Request)} if the teamInSeasonDTO is not valid,
     * or with status {@code 404 (Not Found)} if the teamInSeasonDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the teamInSeasonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TeamInSeasonDTO>> partialUpdateTeamInSeason(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TeamInSeasonDTO teamInSeasonDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TeamInSeason partially : {}, {}", id, teamInSeasonDTO);
        if (teamInSeasonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teamInSeasonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return teamInSeasonRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TeamInSeasonDTO> result = teamInSeasonService.partialUpdate(teamInSeasonDTO);

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
     * {@code GET  /team-in-seasons} : get all the teamInSeasons.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of teamInSeasons in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TeamInSeasonDTO>>> getAllTeamInSeasons(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of TeamInSeasons");
        return teamInSeasonService
            .countAll()
            .zipWith(teamInSeasonService.findAll(pageable).collectList())
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
     * {@code GET  /team-in-seasons/:id} : get the "id" teamInSeason.
     *
     * @param id the id of the teamInSeasonDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teamInSeasonDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TeamInSeasonDTO>> getTeamInSeason(@PathVariable("id") Long id) {
        log.debug("REST request to get TeamInSeason : {}", id);
        Mono<TeamInSeasonDTO> teamInSeasonDTO = teamInSeasonService.findOne(id);
        return ResponseUtil.wrapOrNotFound(teamInSeasonDTO);
    }

    /**
     * {@code DELETE  /team-in-seasons/:id} : delete the "id" teamInSeason.
     *
     * @param id the id of the teamInSeasonDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTeamInSeason(@PathVariable("id") Long id) {
        log.debug("REST request to delete TeamInSeason : {}", id);
        return teamInSeasonService
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
     * {@code SEARCH  /team-in-seasons/_search?query=:query} : search for the teamInSeason corresponding
     * to the query.
     *
     * @param query the query of the teamInSeason search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<TeamInSeasonDTO>>> searchTeamInSeasons(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of TeamInSeasons for query {}", query);
        return teamInSeasonService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(teamInSeasonService.search(query, pageable)));
    }
}
