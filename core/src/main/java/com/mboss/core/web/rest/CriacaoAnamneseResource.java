package com.mboss.core.web.rest;

import com.mboss.core.domain.CriacaoAnamnese;
import com.mboss.core.repository.CriacaoAnamneseRepository;
import com.mboss.core.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mboss.core.domain.CriacaoAnamnese}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CriacaoAnamneseResource {

    private final Logger log = LoggerFactory.getLogger(CriacaoAnamneseResource.class);

    private static final String ENTITY_NAME = "coreCriacaoAnamnese";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CriacaoAnamneseRepository criacaoAnamneseRepository;

    public CriacaoAnamneseResource(CriacaoAnamneseRepository criacaoAnamneseRepository) {
        this.criacaoAnamneseRepository = criacaoAnamneseRepository;
    }

    /**
     * {@code POST  /criacao-anamnese} : Create a new criacaoAnamnese.
     *
     * @param criacaoAnamnese the criacaoAnamnese to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new criacaoAnamnese, or with status {@code 400 (Bad Request)} if the criacaoAnamnese has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/criacao-anamnese")
    public Mono<ResponseEntity<CriacaoAnamnese>> createCriacaoAnamnese(@RequestBody CriacaoAnamnese criacaoAnamnese)
        throws URISyntaxException {
        log.debug("REST request to save CriacaoAnamnese : {}", criacaoAnamnese);
        if (criacaoAnamnese.getId() != null) {
            throw new BadRequestAlertException("A new criacaoAnamnese cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return criacaoAnamneseRepository
            .save(criacaoAnamnese)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/criacao-anamnese/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /criacao-anamnese/:id} : Updates an existing criacaoAnamnese.
     *
     * @param id the id of the criacaoAnamnese to save.
     * @param criacaoAnamnese the criacaoAnamnese to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated criacaoAnamnese,
     * or with status {@code 400 (Bad Request)} if the criacaoAnamnese is not valid,
     * or with status {@code 500 (Internal Server Error)} if the criacaoAnamnese couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/criacao-anamnese/{id}")
    public Mono<ResponseEntity<CriacaoAnamnese>> updateCriacaoAnamnese(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CriacaoAnamnese criacaoAnamnese
    ) throws URISyntaxException {
        log.debug("REST request to update CriacaoAnamnese : {}, {}", id, criacaoAnamnese);
        if (criacaoAnamnese.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, criacaoAnamnese.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return criacaoAnamneseRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return criacaoAnamneseRepository
                        .save(criacaoAnamnese)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            result ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString())
                                    )
                                    .body(result)
                        );
                }
            );
    }

    /**
     * {@code PATCH  /criacao-anamnese/:id} : Partial updates given fields of an existing criacaoAnamnese, field will ignore if it is null
     *
     * @param id the id of the criacaoAnamnese to save.
     * @param criacaoAnamnese the criacaoAnamnese to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated criacaoAnamnese,
     * or with status {@code 400 (Bad Request)} if the criacaoAnamnese is not valid,
     * or with status {@code 404 (Not Found)} if the criacaoAnamnese is not found,
     * or with status {@code 500 (Internal Server Error)} if the criacaoAnamnese couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/criacao-anamnese/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<CriacaoAnamnese>> partialUpdateCriacaoAnamnese(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CriacaoAnamnese criacaoAnamnese
    ) throws URISyntaxException {
        log.debug("REST request to partial update CriacaoAnamnese partially : {}, {}", id, criacaoAnamnese);
        if (criacaoAnamnese.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, criacaoAnamnese.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return criacaoAnamneseRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<CriacaoAnamnese> result = criacaoAnamneseRepository
                        .findById(criacaoAnamnese.getId())
                        .map(
                            existingCriacaoAnamnese -> {
                                if (criacaoAnamnese.getDescricao() != null) {
                                    existingCriacaoAnamnese.setDescricao(criacaoAnamnese.getDescricao());
                                }
                                if (criacaoAnamnese.getIdMedico() != null) {
                                    existingCriacaoAnamnese.setIdMedico(criacaoAnamnese.getIdMedico());
                                }

                                return existingCriacaoAnamnese;
                            }
                        )
                        .flatMap(criacaoAnamneseRepository::save);

                    return result
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            res ->
                                ResponseEntity
                                    .ok()
                                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                                    .body(res)
                        );
                }
            );
    }

    /**
     * {@code GET  /criacao-anamnese} : get all the criacaoAnamnese.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of criacaoAnamnese in body.
     */
    @GetMapping("/criacao-anamnese")
    public Mono<ResponseEntity<List<CriacaoAnamnese>>> getAllCriacaoAnamnese(
        Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of CriacaoAnamnese");
        return criacaoAnamneseRepository
            .count()
            .zipWith(criacaoAnamneseRepository.findAllBy(pageable).collectList())
            .map(
                countWithEntities -> {
                    return ResponseEntity
                        .ok()
                        .headers(
                            PaginationUtil.generatePaginationHttpHeaders(
                                UriComponentsBuilder.fromHttpRequest(request),
                                new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                            )
                        )
                        .body(countWithEntities.getT2());
                }
            );
    }

    /**
     * {@code GET  /criacao-anamnese/:id} : get the "id" criacaoAnamnese.
     *
     * @param id the id of the criacaoAnamnese to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the criacaoAnamnese, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/criacao-anamnese/{id}")
    public Mono<ResponseEntity<CriacaoAnamnese>> getCriacaoAnamnese(@PathVariable Long id) {
        log.debug("REST request to get CriacaoAnamnese : {}", id);
        Mono<CriacaoAnamnese> criacaoAnamnese = criacaoAnamneseRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(criacaoAnamnese);
    }

    /**
     * {@code DELETE  /criacao-anamnese/:id} : delete the "id" criacaoAnamnese.
     *
     * @param id the id of the criacaoAnamnese to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/criacao-anamnese/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCriacaoAnamnese(@PathVariable Long id) {
        log.debug("REST request to delete CriacaoAnamnese : {}", id);
        return criacaoAnamneseRepository
            .deleteById(id)
            .map(
                result ->
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
            );
    }
}
