package com.mboss.core.web.rest;

import com.mboss.core.domain.CriacaoRaca;
import com.mboss.core.repository.CriacaoRacaRepository;
import com.mboss.core.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mboss.core.domain.CriacaoRaca}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CriacaoRacaResource {

    private final Logger log = LoggerFactory.getLogger(CriacaoRacaResource.class);

    private static final String ENTITY_NAME = "coreCriacaoRaca";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CriacaoRacaRepository criacaoRacaRepository;

    public CriacaoRacaResource(CriacaoRacaRepository criacaoRacaRepository) {
        this.criacaoRacaRepository = criacaoRacaRepository;
    }

    /**
     * {@code POST  /criacao-racas} : Create a new criacaoRaca.
     *
     * @param criacaoRaca the criacaoRaca to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new criacaoRaca, or with status {@code 400 (Bad Request)} if the criacaoRaca has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/criacao-racas")
    public Mono<ResponseEntity<CriacaoRaca>> createCriacaoRaca(@RequestBody CriacaoRaca criacaoRaca) throws URISyntaxException {
        log.debug("REST request to save CriacaoRaca : {}", criacaoRaca);
        if (criacaoRaca.getId() != null) {
            throw new BadRequestAlertException("A new criacaoRaca cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return criacaoRacaRepository
            .save(criacaoRaca)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/criacao-racas/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /criacao-racas/:id} : Updates an existing criacaoRaca.
     *
     * @param id the id of the criacaoRaca to save.
     * @param criacaoRaca the criacaoRaca to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated criacaoRaca,
     * or with status {@code 400 (Bad Request)} if the criacaoRaca is not valid,
     * or with status {@code 500 (Internal Server Error)} if the criacaoRaca couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/criacao-racas/{id}")
    public Mono<ResponseEntity<CriacaoRaca>> updateCriacaoRaca(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CriacaoRaca criacaoRaca
    ) throws URISyntaxException {
        log.debug("REST request to update CriacaoRaca : {}, {}", id, criacaoRaca);
        if (criacaoRaca.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, criacaoRaca.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return criacaoRacaRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return criacaoRacaRepository
                        .save(criacaoRaca)
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
     * {@code PATCH  /criacao-racas/:id} : Partial updates given fields of an existing criacaoRaca, field will ignore if it is null
     *
     * @param id the id of the criacaoRaca to save.
     * @param criacaoRaca the criacaoRaca to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated criacaoRaca,
     * or with status {@code 400 (Bad Request)} if the criacaoRaca is not valid,
     * or with status {@code 404 (Not Found)} if the criacaoRaca is not found,
     * or with status {@code 500 (Internal Server Error)} if the criacaoRaca couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/criacao-racas/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<CriacaoRaca>> partialUpdateCriacaoRaca(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CriacaoRaca criacaoRaca
    ) throws URISyntaxException {
        log.debug("REST request to partial update CriacaoRaca partially : {}, {}", id, criacaoRaca);
        if (criacaoRaca.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, criacaoRaca.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return criacaoRacaRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<CriacaoRaca> result = criacaoRacaRepository
                        .findById(criacaoRaca.getId())
                        .map(
                            existingCriacaoRaca -> {
                                if (criacaoRaca.getDescricao() != null) {
                                    existingCriacaoRaca.setDescricao(criacaoRaca.getDescricao());
                                }
                                if (criacaoRaca.getAtivo() != null) {
                                    existingCriacaoRaca.setAtivo(criacaoRaca.getAtivo());
                                }

                                return existingCriacaoRaca;
                            }
                        )
                        .flatMap(criacaoRacaRepository::save);

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
     * {@code GET  /criacao-racas} : get all the criacaoRacas.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of criacaoRacas in body.
     */
    @GetMapping("/criacao-racas")
    public Mono<List<CriacaoRaca>> getAllCriacaoRacas(@RequestParam(required = false) String filter) {
        if ("criacao-is-null".equals(filter)) {
            log.debug("REST request to get all CriacaoRacas where criacao is null");
            return criacaoRacaRepository.findAllWhereCriacaoIsNull().collectList();
        }
        log.debug("REST request to get all CriacaoRacas");
        return criacaoRacaRepository.findAll().collectList();
    }

    /**
     * {@code GET  /criacao-racas} : get all the criacaoRacas as a stream.
     * @return the {@link Flux} of criacaoRacas.
     */
    @GetMapping(value = "/criacao-racas", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<CriacaoRaca> getAllCriacaoRacasAsStream() {
        log.debug("REST request to get all CriacaoRacas as a stream");
        return criacaoRacaRepository.findAll();
    }

    /**
     * {@code GET  /criacao-racas/:id} : get the "id" criacaoRaca.
     *
     * @param id the id of the criacaoRaca to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the criacaoRaca, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/criacao-racas/{id}")
    public Mono<ResponseEntity<CriacaoRaca>> getCriacaoRaca(@PathVariable Long id) {
        log.debug("REST request to get CriacaoRaca : {}", id);
        Mono<CriacaoRaca> criacaoRaca = criacaoRacaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(criacaoRaca);
    }

    /**
     * {@code DELETE  /criacao-racas/:id} : delete the "id" criacaoRaca.
     *
     * @param id the id of the criacaoRaca to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/criacao-racas/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCriacaoRaca(@PathVariable Long id) {
        log.debug("REST request to delete CriacaoRaca : {}", id);
        return criacaoRacaRepository
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
