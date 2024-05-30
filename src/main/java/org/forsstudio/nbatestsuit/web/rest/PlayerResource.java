package org.forsstudio.nbatestsuit.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.forsstudio.nbatestsuit.repository.PlayerRepository;
import org.forsstudio.nbatestsuit.service.PlayerService;
import org.forsstudio.nbatestsuit.service.dto.PlayerDTO;
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
 * REST controller for managing {@link org.forsstudio.nbatestsuit.domain.Player}.
 */
@RestController
@RequestMapping("/api/players")
public class PlayerResource {

    private final Logger log = LoggerFactory.getLogger(PlayerResource.class);

    private static final String ENTITY_NAME = "player";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlayerService playerService;

    private final PlayerRepository playerRepository;

    public PlayerResource(PlayerService playerService, PlayerRepository playerRepository) {
        this.playerService = playerService;
        this.playerRepository = playerRepository;
    }

    /**
     * {@code POST  /players} : Create a new player.
     *
     * @param playerDTO the playerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new playerDTO, or with status {@code 400 (Bad Request)} if the player has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<PlayerDTO>> createPlayer(@RequestBody PlayerDTO playerDTO) throws URISyntaxException {
        log.debug("REST request to save Player : {}", playerDTO);
        if (playerDTO.getId() != null) {
            throw new BadRequestAlertException("A new player cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return playerService
            .save(playerDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/players/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /players/:id} : Updates an existing player.
     *
     * @param id the id of the playerDTO to save.
     * @param playerDTO the playerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated playerDTO,
     * or with status {@code 400 (Bad Request)} if the playerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the playerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<PlayerDTO>> updatePlayer(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PlayerDTO playerDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Player : {}, {}", id, playerDTO);
        if (playerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, playerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return playerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return playerService
                    .update(playerDTO)
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
     * {@code PATCH  /players/:id} : Partial updates given fields of an existing player, field will ignore if it is null
     *
     * @param id the id of the playerDTO to save.
     * @param playerDTO the playerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated playerDTO,
     * or with status {@code 400 (Bad Request)} if the playerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the playerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the playerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<PlayerDTO>> partialUpdatePlayer(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PlayerDTO playerDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Player partially : {}, {}", id, playerDTO);
        if (playerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, playerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return playerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<PlayerDTO> result = playerService.partialUpdate(playerDTO);

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
     * {@code GET  /players} : get all the players.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of players in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<PlayerDTO>>> getAllPlayers(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Players");
        return playerService
            .countAll()
            .zipWith(playerService.findAll(pageable).collectList())
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
     * {@code GET  /players/:id} : get the "id" player.
     *
     * @param id the id of the playerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the playerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<PlayerDTO>> getPlayer(@PathVariable("id") Long id) {
        log.debug("REST request to get Player : {}", id);
        Mono<PlayerDTO> playerDTO = playerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(playerDTO);
    }

    /**
     * {@code DELETE  /players/:id} : delete the "id" player.
     *
     * @param id the id of the playerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePlayer(@PathVariable("id") Long id) {
        log.debug("REST request to delete Player : {}", id);
        return playerService
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
     * {@code SEARCH  /players/_search?query=:query} : search for the player corresponding
     * to the query.
     *
     * @param query the query of the player search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<PlayerDTO>>> searchPlayers(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Players for query {}", query);
        return playerService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(playerService.search(query, pageable)));
    }
}
