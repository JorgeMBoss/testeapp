package com.mboss.core.web.rest;

import com.mboss.core.domain.CriacaoConsumo;
import com.mboss.core.repository.CriacaoConsumoRepository;
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
 * REST controller for managing {@link com.mboss.core.domain.CriacaoConsumo}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CriacaoConsumoResource {

    private final Logger log = LoggerFactory.getLogger(CriacaoConsumoResource.class);

    private static final String ENTITY_NAME = "coreCriacaoConsumo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CriacaoConsumoRepository criacaoConsumoRepository;

    public CriacaoConsumoResource(CriacaoConsumoRepository criacaoConsumoRepository) {
        this.criacaoConsumoRepository = criacaoConsumoRepository;
    }

    /**
     * {@code POST  /criacao-consumos} : Create a new criacaoConsumo.
     *
     * @param criacaoConsumo the criacaoConsumo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new criacaoConsumo, or with status {@code 400 (Bad Request)} if the criacaoConsumo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/criacao-consumos")
    public Mono<ResponseEntity<CriacaoConsumo>> createCriacaoConsumo(@RequestBody CriacaoConsumo criacaoConsumo) throws URISyntaxException {
        log.debug("REST request to save CriacaoConsumo : {}", criacaoConsumo);
        if (criacaoConsumo.getId() != null) {
            throw new BadRequestAlertException("A new criacaoConsumo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return criacaoConsumoRepository
            .save(criacaoConsumo)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/criacao-consumos/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /criacao-consumos/:id} : Updates an existing criacaoConsumo.
     *
     * @param id the id of the criacaoConsumo to save.
     * @param criacaoConsumo the criacaoConsumo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated criacaoConsumo,
     * or with status {@code 400 (Bad Request)} if the criacaoConsumo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the criacaoConsumo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/criacao-consumos/{id}")
    public Mono<ResponseEntity<CriacaoConsumo>> updateCriacaoConsumo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CriacaoConsumo criacaoConsumo
    ) throws URISyntaxException {
        log.debug("REST request to update CriacaoConsumo : {}, {}", id, criacaoConsumo);
        if (criacaoConsumo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, criacaoConsumo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return criacaoConsumoRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return criacaoConsumoRepository
                        .save(criacaoConsumo)
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
     * {@code PATCH  /criacao-consumos/:id} : Partial updates given fields of an existing criacaoConsumo, field will ignore if it is null
     *
     * @param id the id of the criacaoConsumo to save.
     * @param criacaoConsumo the criacaoConsumo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated criacaoConsumo,
     * or with status {@code 400 (Bad Request)} if the criacaoConsumo is not valid,
     * or with status {@code 404 (Not Found)} if the criacaoConsumo is not found,
     * or with status {@code 500 (Internal Server Error)} if the criacaoConsumo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/criacao-consumos/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<CriacaoConsumo>> partialUpdateCriacaoConsumo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CriacaoConsumo criacaoConsumo
    ) throws URISyntaxException {
        log.debug("REST request to partial update CriacaoConsumo partially : {}, {}", id, criacaoConsumo);
        if (criacaoConsumo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, criacaoConsumo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return criacaoConsumoRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<CriacaoConsumo> result = criacaoConsumoRepository
                        .findById(criacaoConsumo.getId())
                        .map(
                            existingCriacaoConsumo -> {
                                if (criacaoConsumo.getDataSistema() != null) {
                                    existingCriacaoConsumo.setDataSistema(criacaoConsumo.getDataSistema());
                                }
                                if (criacaoConsumo.getDataVenda() != null) {
                                    existingCriacaoConsumo.setDataVenda(criacaoConsumo.getDataVenda());
                                }
                                if (criacaoConsumo.getDataAviso() != null) {
                                    existingCriacaoConsumo.setDataAviso(criacaoConsumo.getDataAviso());
                                }
                                if (criacaoConsumo.getAnotacao() != null) {
                                    existingCriacaoConsumo.setAnotacao(criacaoConsumo.getAnotacao());
                                }
                                if (criacaoConsumo.getStatus() != null) {
                                    existingCriacaoConsumo.setStatus(criacaoConsumo.getStatus());
                                }

                                return existingCriacaoConsumo;
                            }
                        )
                        .flatMap(criacaoConsumoRepository::save);

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
     * {@code GET  /criacao-consumos} : get all the criacaoConsumos.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of criacaoConsumos in body.
     */
    @GetMapping("/criacao-consumos")
    public Mono<ResponseEntity<List<CriacaoConsumo>>> getAllCriacaoConsumos(
        Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of CriacaoConsumos");
        return criacaoConsumoRepository
            .count()
            .zipWith(criacaoConsumoRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /criacao-consumos/:id} : get the "id" criacaoConsumo.
     *
     * @param id the id of the criacaoConsumo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the criacaoConsumo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/criacao-consumos/{id}")
    public Mono<ResponseEntity<CriacaoConsumo>> getCriacaoConsumo(@PathVariable Long id) {
        log.debug("REST request to get CriacaoConsumo : {}", id);
        Mono<CriacaoConsumo> criacaoConsumo = criacaoConsumoRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(criacaoConsumo);
    }

    /**
     * {@code DELETE  /criacao-consumos/:id} : delete the "id" criacaoConsumo.
     *
     * @param id the id of the criacaoConsumo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/criacao-consumos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCriacaoConsumo(@PathVariable Long id) {
        log.debug("REST request to delete CriacaoConsumo : {}", id);
        return criacaoConsumoRepository
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
