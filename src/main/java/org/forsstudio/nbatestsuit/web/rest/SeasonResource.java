package org.forsstudio.nbatestsuit.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.forsstudio.nbatestsuit.repository.SeasonRepository;
import org.forsstudio.nbatestsuit.service.SeasonService;
import org.forsstudio.nbatestsuit.service.dto.SeasonDTO;
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
 * REST controller for managing {@link org.forsstudio.nbatestsuit.domain.Season}.
 */
@RestController
@RequestMapping("/api/seasons")
public class SeasonResource {

    private final Logger log = LoggerFactory.getLogger(SeasonResource.class);

    private static final String ENTITY_NAME = "season";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SeasonService seasonService;

    private final SeasonRepository seasonRepository;

    public SeasonResource(SeasonService seasonService, SeasonRepository seasonRepository) {
        this.seasonService = seasonService;
        this.seasonRepository = seasonRepository;
    }

    /**
     * {@code POST  /seasons} : Create a new season.
     *
     * @param seasonDTO the seasonDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new seasonDTO, or with status {@code 400 (Bad Request)} if the season has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<SeasonDTO>> createSeason(@RequestBody SeasonDTO seasonDTO) throws URISyntaxException {
        log.debug("REST request to save Season : {}", seasonDTO);
        if (seasonDTO.getId() != null) {
            throw new BadRequestAlertException("A new season cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return seasonService
            .save(seasonDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/seasons/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /seasons/:id} : Updates an existing season.
     *
     * @param id the id of the seasonDTO to save.
     * @param seasonDTO the seasonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated seasonDTO,
     * or with status {@code 400 (Bad Request)} if the seasonDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the seasonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<SeasonDTO>> updateSeason(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SeasonDTO seasonDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Season : {}, {}", id, seasonDTO);
        if (seasonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, seasonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return seasonRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return seasonService
                    .update(seasonDTO)
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
     * {@code PATCH  /seasons/:id} : Partial updates given fields of an existing season, field will ignore if it is null
     *
     * @param id the id of the seasonDTO to save.
     * @param seasonDTO the seasonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated seasonDTO,
     * or with status {@code 400 (Bad Request)} if the seasonDTO is not valid,
     * or with status {@code 404 (Not Found)} if the seasonDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the seasonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SeasonDTO>> partialUpdateSeason(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SeasonDTO seasonDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Season partially : {}, {}", id, seasonDTO);
        if (seasonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, seasonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return seasonRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<SeasonDTO> result = seasonService.partialUpdate(seasonDTO);

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
     * {@code GET  /seasons} : get all the seasons.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of seasons in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<SeasonDTO>>> getAllSeasons(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Seasons");
        return seasonService
            .countAll()
            .zipWith(seasonService.findAll(pageable).collectList())
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
     * {@code GET  /seasons/:id} : get the "id" season.
     *
     * @param id the id of the seasonDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the seasonDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SeasonDTO>> getSeason(@PathVariable("id") Long id) {
        log.debug("REST request to get Season : {}", id);
        Mono<SeasonDTO> seasonDTO = seasonService.findOne(id);
        return ResponseUtil.wrapOrNotFound(seasonDTO);
    }

    /**
     * {@code DELETE  /seasons/:id} : delete the "id" season.
     *
     * @param id the id of the seasonDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSeason(@PathVariable("id") Long id) {
        log.debug("REST request to delete Season : {}", id);
        return seasonService
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
     * {@code SEARCH  /seasons/_search?query=:query} : search for the season corresponding
     * to the query.
     *
     * @param query the query of the season search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<SeasonDTO>>> searchSeasons(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Seasons for query {}", query);
        return seasonService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(
                page ->
                    PaginationUtil.generatePaginationHttpHeaders(
                        ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                        page
                    )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(seasonService.search(query, pageable)));
    }
}
