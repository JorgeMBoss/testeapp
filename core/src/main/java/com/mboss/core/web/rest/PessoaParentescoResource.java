package com.mboss.core.web.rest;

import com.mboss.core.domain.PessoaParentesco;
import com.mboss.core.repository.PessoaParentescoRepository;
import com.mboss.core.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
 * REST controller for managing {@link com.mboss.core.domain.PessoaParentesco}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PessoaParentescoResource {

    private final Logger log = LoggerFactory.getLogger(PessoaParentescoResource.class);

    private static final String ENTITY_NAME = "corePessoaParentesco";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PessoaParentescoRepository pessoaParentescoRepository;

    public PessoaParentescoResource(PessoaParentescoRepository pessoaParentescoRepository) {
        this.pessoaParentescoRepository = pessoaParentescoRepository;
    }

    /**
     * {@code POST  /pessoa-parentescos} : Create a new pessoaParentesco.
     *
     * @param pessoaParentesco the pessoaParentesco to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pessoaParentesco, or with status {@code 400 (Bad Request)} if the pessoaParentesco has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pessoa-parentescos")
    public Mono<ResponseEntity<PessoaParentesco>> createPessoaParentesco(@Valid @RequestBody PessoaParentesco pessoaParentesco)
        throws URISyntaxException {
        log.debug("REST request to save PessoaParentesco : {}", pessoaParentesco);
        if (pessoaParentesco.getId() != null) {
            throw new BadRequestAlertException("A new pessoaParentesco cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return pessoaParentescoRepository
            .save(pessoaParentesco)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/pessoa-parentescos/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /pessoa-parentescos/:id} : Updates an existing pessoaParentesco.
     *
     * @param id the id of the pessoaParentesco to save.
     * @param pessoaParentesco the pessoaParentesco to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaParentesco,
     * or with status {@code 400 (Bad Request)} if the pessoaParentesco is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pessoaParentesco couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pessoa-parentescos/{id}")
    public Mono<ResponseEntity<PessoaParentesco>> updatePessoaParentesco(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PessoaParentesco pessoaParentesco
    ) throws URISyntaxException {
        log.debug("REST request to update PessoaParentesco : {}, {}", id, pessoaParentesco);
        if (pessoaParentesco.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaParentesco.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaParentescoRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return pessoaParentescoRepository
                        .save(pessoaParentesco)
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
     * {@code PATCH  /pessoa-parentescos/:id} : Partial updates given fields of an existing pessoaParentesco, field will ignore if it is null
     *
     * @param id the id of the pessoaParentesco to save.
     * @param pessoaParentesco the pessoaParentesco to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaParentesco,
     * or with status {@code 400 (Bad Request)} if the pessoaParentesco is not valid,
     * or with status {@code 404 (Not Found)} if the pessoaParentesco is not found,
     * or with status {@code 500 (Internal Server Error)} if the pessoaParentesco couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pessoa-parentescos/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<PessoaParentesco>> partialUpdatePessoaParentesco(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PessoaParentesco pessoaParentesco
    ) throws URISyntaxException {
        log.debug("REST request to partial update PessoaParentesco partially : {}, {}", id, pessoaParentesco);
        if (pessoaParentesco.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaParentesco.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaParentescoRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<PessoaParentesco> result = pessoaParentescoRepository
                        .findById(pessoaParentesco.getId())
                        .map(
                            existingPessoaParentesco -> {
                                if (pessoaParentesco.getCpf() != null) {
                                    existingPessoaParentesco.setCpf(pessoaParentesco.getCpf());
                                }
                                if (pessoaParentesco.getNome() != null) {
                                    existingPessoaParentesco.setNome(pessoaParentesco.getNome());
                                }
                                if (pessoaParentesco.getEmail() != null) {
                                    existingPessoaParentesco.setEmail(pessoaParentesco.getEmail());
                                }

                                return existingPessoaParentesco;
                            }
                        )
                        .flatMap(pessoaParentescoRepository::save);

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
     * {@code GET  /pessoa-parentescos} : get all the pessoaParentescos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pessoaParentescos in body.
     */
    @GetMapping("/pessoa-parentescos")
    public Mono<List<PessoaParentesco>> getAllPessoaParentescos() {
        log.debug("REST request to get all PessoaParentescos");
        return pessoaParentescoRepository.findAll().collectList();
    }

    /**
     * {@code GET  /pessoa-parentescos} : get all the pessoaParentescos as a stream.
     * @return the {@link Flux} of pessoaParentescos.
     */
    @GetMapping(value = "/pessoa-parentescos", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PessoaParentesco> getAllPessoaParentescosAsStream() {
        log.debug("REST request to get all PessoaParentescos as a stream");
        return pessoaParentescoRepository.findAll();
    }

    /**
     * {@code GET  /pessoa-parentescos/:id} : get the "id" pessoaParentesco.
     *
     * @param id the id of the pessoaParentesco to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pessoaParentesco, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pessoa-parentescos/{id}")
    public Mono<ResponseEntity<PessoaParentesco>> getPessoaParentesco(@PathVariable Long id) {
        log.debug("REST request to get PessoaParentesco : {}", id);
        Mono<PessoaParentesco> pessoaParentesco = pessoaParentescoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pessoaParentesco);
    }

    /**
     * {@code DELETE  /pessoa-parentescos/:id} : delete the "id" pessoaParentesco.
     *
     * @param id the id of the pessoaParentesco to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pessoa-parentescos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePessoaParentesco(@PathVariable Long id) {
        log.debug("REST request to delete PessoaParentesco : {}", id);
        return pessoaParentescoRepository
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
