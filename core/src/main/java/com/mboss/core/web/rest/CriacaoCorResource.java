package com.mboss.core.web.rest;

import com.mboss.core.domain.CriacaoCor;
import com.mboss.core.repository.CriacaoCorRepository;
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
 * REST controller for managing {@link com.mboss.core.domain.CriacaoCor}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CriacaoCorResource {

    private final Logger log = LoggerFactory.getLogger(CriacaoCorResource.class);

    private static final String ENTITY_NAME = "coreCriacaoCor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CriacaoCorRepository criacaoCorRepository;

    public CriacaoCorResource(CriacaoCorRepository criacaoCorRepository) {
        this.criacaoCorRepository = criacaoCorRepository;
    }

    /**
     * {@code POST  /criacao-cors} : Create a new criacaoCor.
     *
     * @param criacaoCor the criacaoCor to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new criacaoCor, or with status {@code 400 (Bad Request)} if the criacaoCor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/criacao-cors")
    public Mono<ResponseEntity<CriacaoCor>> createCriacaoCor(@RequestBody CriacaoCor criacaoCor) throws URISyntaxException {
        log.debug("REST request to save CriacaoCor : {}", criacaoCor);
        if (criacaoCor.getId() != null) {
            throw new BadRequestAlertException("A new criacaoCor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return criacaoCorRepository
            .save(criacaoCor)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/criacao-cors/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /criacao-cors/:id} : Updates an existing criacaoCor.
     *
     * @param id the id of the criacaoCor to save.
     * @param criacaoCor the criacaoCor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated criacaoCor,
     * or with status {@code 400 (Bad Request)} if the criacaoCor is not valid,
     * or with status {@code 500 (Internal Server Error)} if the criacaoCor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/criacao-cors/{id}")
    public Mono<ResponseEntity<CriacaoCor>> updateCriacaoCor(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CriacaoCor criacaoCor
    ) throws URISyntaxException {
        log.debug("REST request to update CriacaoCor : {}, {}", id, criacaoCor);
        if (criacaoCor.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, criacaoCor.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return criacaoCorRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return criacaoCorRepository
                        .save(criacaoCor)
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
     * {@code PATCH  /criacao-cors/:id} : Partial updates given fields of an existing criacaoCor, field will ignore if it is null
     *
     * @param id the id of the criacaoCor to save.
     * @param criacaoCor the criacaoCor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated criacaoCor,
     * or with status {@code 400 (Bad Request)} if the criacaoCor is not valid,
     * or with status {@code 404 (Not Found)} if the criacaoCor is not found,
     * or with status {@code 500 (Internal Server Error)} if the criacaoCor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/criacao-cors/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<CriacaoCor>> partialUpdateCriacaoCor(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CriacaoCor criacaoCor
    ) throws URISyntaxException {
        log.debug("REST request to partial update CriacaoCor partially : {}, {}", id, criacaoCor);
        if (criacaoCor.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, criacaoCor.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return criacaoCorRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<CriacaoCor> result = criacaoCorRepository
                        .findById(criacaoCor.getId())
                        .map(
                            existingCriacaoCor -> {
                                if (criacaoCor.getDescricao() != null) {
                                    existingCriacaoCor.setDescricao(criacaoCor.getDescricao());
                                }
                                if (criacaoCor.getAtivo() != null) {
                                    existingCriacaoCor.setAtivo(criacaoCor.getAtivo());
                                }

                                return existingCriacaoCor;
                            }
                        )
                        .flatMap(criacaoCorRepository::save);

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
     * {@code GET  /criacao-cors} : get all the criacaoCors.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of criacaoCors in body.
     */
    @GetMapping("/criacao-cors")
    public Mono<List<CriacaoCor>> getAllCriacaoCors(@RequestParam(required = false) String filter) {
        if ("criacao-is-null".equals(filter)) {
            log.debug("REST request to get all CriacaoCors where criacao is null");
            return criacaoCorRepository.findAllWhereCriacaoIsNull().collectList();
        }
        log.debug("REST request to get all CriacaoCors");
        return criacaoCorRepository.findAll().collectList();
    }

    /**
     * {@code GET  /criacao-cors} : get all the criacaoCors as a stream.
     * @return the {@link Flux} of criacaoCors.
     */
    @GetMapping(value = "/criacao-cors", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<CriacaoCor> getAllCriacaoCorsAsStream() {
        log.debug("REST request to get all CriacaoCors as a stream");
        return criacaoCorRepository.findAll();
    }

    /**
     * {@code GET  /criacao-cors/:id} : get the "id" criacaoCor.
     *
     * @param id the id of the criacaoCor to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the criacaoCor, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/criacao-cors/{id}")
    public Mono<ResponseEntity<CriacaoCor>> getCriacaoCor(@PathVariable Long id) {
        log.debug("REST request to get CriacaoCor : {}", id);
        Mono<CriacaoCor> criacaoCor = criacaoCorRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(criacaoCor);
    }

    /**
     * {@code DELETE  /criacao-cors/:id} : delete the "id" criacaoCor.
     *
     * @param id the id of the criacaoCor to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/criacao-cors/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCriacaoCor(@PathVariable Long id) {
        log.debug("REST request to delete CriacaoCor : {}", id);
        return criacaoCorRepository
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
