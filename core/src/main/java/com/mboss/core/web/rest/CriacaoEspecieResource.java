package com.mboss.core.web.rest;

import com.mboss.core.domain.CriacaoEspecie;
import com.mboss.core.repository.CriacaoEspecieRepository;
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
 * REST controller for managing {@link com.mboss.core.domain.CriacaoEspecie}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CriacaoEspecieResource {

    private final Logger log = LoggerFactory.getLogger(CriacaoEspecieResource.class);

    private static final String ENTITY_NAME = "coreCriacaoEspecie";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CriacaoEspecieRepository criacaoEspecieRepository;

    public CriacaoEspecieResource(CriacaoEspecieRepository criacaoEspecieRepository) {
        this.criacaoEspecieRepository = criacaoEspecieRepository;
    }

    /**
     * {@code POST  /criacao-especies} : Create a new criacaoEspecie.
     *
     * @param criacaoEspecie the criacaoEspecie to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new criacaoEspecie, or with status {@code 400 (Bad Request)} if the criacaoEspecie has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/criacao-especies")
    public Mono<ResponseEntity<CriacaoEspecie>> createCriacaoEspecie(@RequestBody CriacaoEspecie criacaoEspecie) throws URISyntaxException {
        log.debug("REST request to save CriacaoEspecie : {}", criacaoEspecie);
        if (criacaoEspecie.getId() != null) {
            throw new BadRequestAlertException("A new criacaoEspecie cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return criacaoEspecieRepository
            .save(criacaoEspecie)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/criacao-especies/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /criacao-especies/:id} : Updates an existing criacaoEspecie.
     *
     * @param id the id of the criacaoEspecie to save.
     * @param criacaoEspecie the criacaoEspecie to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated criacaoEspecie,
     * or with status {@code 400 (Bad Request)} if the criacaoEspecie is not valid,
     * or with status {@code 500 (Internal Server Error)} if the criacaoEspecie couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/criacao-especies/{id}")
    public Mono<ResponseEntity<CriacaoEspecie>> updateCriacaoEspecie(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CriacaoEspecie criacaoEspecie
    ) throws URISyntaxException {
        log.debug("REST request to update CriacaoEspecie : {}, {}", id, criacaoEspecie);
        if (criacaoEspecie.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, criacaoEspecie.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return criacaoEspecieRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return criacaoEspecieRepository
                        .save(criacaoEspecie)
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
     * {@code PATCH  /criacao-especies/:id} : Partial updates given fields of an existing criacaoEspecie, field will ignore if it is null
     *
     * @param id the id of the criacaoEspecie to save.
     * @param criacaoEspecie the criacaoEspecie to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated criacaoEspecie,
     * or with status {@code 400 (Bad Request)} if the criacaoEspecie is not valid,
     * or with status {@code 404 (Not Found)} if the criacaoEspecie is not found,
     * or with status {@code 500 (Internal Server Error)} if the criacaoEspecie couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/criacao-especies/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<CriacaoEspecie>> partialUpdateCriacaoEspecie(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CriacaoEspecie criacaoEspecie
    ) throws URISyntaxException {
        log.debug("REST request to partial update CriacaoEspecie partially : {}, {}", id, criacaoEspecie);
        if (criacaoEspecie.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, criacaoEspecie.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return criacaoEspecieRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<CriacaoEspecie> result = criacaoEspecieRepository
                        .findById(criacaoEspecie.getId())
                        .map(
                            existingCriacaoEspecie -> {
                                if (criacaoEspecie.getDescricao() != null) {
                                    existingCriacaoEspecie.setDescricao(criacaoEspecie.getDescricao());
                                }
                                if (criacaoEspecie.getAtivo() != null) {
                                    existingCriacaoEspecie.setAtivo(criacaoEspecie.getAtivo());
                                }

                                return existingCriacaoEspecie;
                            }
                        )
                        .flatMap(criacaoEspecieRepository::save);

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
     * {@code GET  /criacao-especies} : get all the criacaoEspecies.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of criacaoEspecies in body.
     */
    @GetMapping("/criacao-especies")
    public Mono<List<CriacaoEspecie>> getAllCriacaoEspecies(@RequestParam(required = false) String filter) {
        if ("criacao-is-null".equals(filter)) {
            log.debug("REST request to get all CriacaoEspecies where criacao is null");
            return criacaoEspecieRepository.findAllWhereCriacaoIsNull().collectList();
        }
        log.debug("REST request to get all CriacaoEspecies");
        return criacaoEspecieRepository.findAll().collectList();
    }

    /**
     * {@code GET  /criacao-especies} : get all the criacaoEspecies as a stream.
     * @return the {@link Flux} of criacaoEspecies.
     */
    @GetMapping(value = "/criacao-especies", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<CriacaoEspecie> getAllCriacaoEspeciesAsStream() {
        log.debug("REST request to get all CriacaoEspecies as a stream");
        return criacaoEspecieRepository.findAll();
    }

    /**
     * {@code GET  /criacao-especies/:id} : get the "id" criacaoEspecie.
     *
     * @param id the id of the criacaoEspecie to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the criacaoEspecie, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/criacao-especies/{id}")
    public Mono<ResponseEntity<CriacaoEspecie>> getCriacaoEspecie(@PathVariable Long id) {
        log.debug("REST request to get CriacaoEspecie : {}", id);
        Mono<CriacaoEspecie> criacaoEspecie = criacaoEspecieRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(criacaoEspecie);
    }

    /**
     * {@code DELETE  /criacao-especies/:id} : delete the "id" criacaoEspecie.
     *
     * @param id the id of the criacaoEspecie to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/criacao-especies/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCriacaoEspecie(@PathVariable Long id) {
        log.debug("REST request to delete CriacaoEspecie : {}", id);
        return criacaoEspecieRepository
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
