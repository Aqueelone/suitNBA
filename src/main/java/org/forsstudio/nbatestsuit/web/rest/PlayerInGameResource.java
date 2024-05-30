package org.forsstudio.nbatestsuit.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.forsstudio.nbatestsuit.repository.PlayerInGameRepository;
import org.forsstudio.nbatestsuit.service.PlayerInGameService;
import org.forsstudio.nbatestsuit.service.dto.PlayerInGameDTO;
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
 * REST controller for managing {@link org.forsstudio.nbatestsuit.domain.PlayerInGame}.
 */
@RestController
@RequestMapping("/api/player-in-games")
public class PlayerInGameResource {

    private final Logger log = LoggerFactory.getLogger(PlayerInGameResource.class);

    private static final String ENTITY_NAME = "playerInGame";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlayerInGameService playerInGameService;

    private final PlayerInGameRepository playerInGameRepository;

    public PlayerInGameResource(PlayerInGameService playerInGameService, PlayerInGameRepository playerInGameRepository) {
        this.playerInGameService = playerInGameService;
        this.playerInGameRepository = playerInGameRepository;
    }

    /**
     * {@code POST  /player-in-games} : Create a new playerInGame.
     *
     * @param playerInGameDTO the playerInGameDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new playerInGameDTO, or with status {@code 400 (Bad Request)} if the playerInGame has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<PlayerInGameDTO>> createPlayerInGame(@RequestBody PlayerInGameDTO playerInGameDTO)
        throws URISyntaxException {
        log.debug("REST request to save PlayerInGame : {}", playerInGameDTO);
        if (playerInGameDTO.getId() != null) {
            throw new BadRequestAlertException("A new playerInGame cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return playerInGameService
            .save(playerInGameDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/player-in-games/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /player-in-games/:id} : Updates an existing playerInGame.
     *
     * @param id the id of the playerInGameDTO to save.
     * @param playerInGameDTO the playerInGameDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated playerInGameDTO,
     * or with status {@code 400 (Bad Request)} if the playerInGameDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the playerInGameDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<PlayerInGameDTO>> updatePlayerInGame(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PlayerInGameDTO playerInGameDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PlayerInGame : {}, {}", id, playerInGameDTO);
        if (playerInGameDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, playerInGameDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return playerInGameRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return playerInGameService
                    .update(playerInGameDTO)
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
     * {@code PATCH  /player-in-games/:id} : Partial updates given fields of an existing playerInGame, field will ignore if it is null
     *
     * @param id the id of the playerInGameDTO to save.
     * @param playerInGameDTO the playerInGameDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated playerInGameDTO,
     * or with status {@code 400 (Bad Request)} if the playerInGameDTO is not valid,
     * or with status {@code 404 (Not Found)} if the playerInGameDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the playerInGameDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<PlayerInGameDTO>> partialUpdatePlayerInGame(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PlayerInGameDTO playerInGameDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PlayerInGame partially : {}, {}", id, playerInGameDTO);
        if (playerInGameDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, playerInGameDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return playerInGameRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<PlayerInGameDTO> result = playerInGameService.partialUpdate(playerInGameDTO);

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
     * {@code GET  /player-in-games} : get all the playerInGames.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of playerInGames in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<PlayerInGameDTO>>> getAllPlayerInGames(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of PlayerInGames");
        return playerInGameService
            .countAll()
            .zipWith(playerInGameService.findAll(pageable).collectList())
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
     * {@code GET  /player-in-games/:id} : get the "id" playerInGame.
     *
     * @param id the id of the playerInGameDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the playerInGameDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<PlayerInGameDTO>> getPlayerInGame(@PathVariable("id") Long id) {
        log.debug("REST request to get PlayerInGame : {}", id);
        Mono<PlayerInGameDTO> playerInGameDTO = playerInGameService.findOne(id);
        return ResponseUtil.wrapOrNotFound(playerInGameDTO);
    }

    /**
     * {@code DELETE  /player-in-games/:id} : delete the "id" playerInGame.
     *
     * @param id the id of the playerInGameDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePlayerInGame(@PathVariable("id") Long id) {
        log.debug("REST request to delete PlayerInGame : {}", id);
        return playerInGameService
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
     * {@code SEARCH  /player-in-games/_search?query=:query} : search for the playerInGame corresponding
     * to the query.
     *
     * @param query the query of the playerInGame search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<PlayerInGameDTO>>> searchPlayerInGames(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of PlayerInGames for query {}", query);
        return playerInGameService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(playerInGameService.search(query, pageable)));
    }
}
