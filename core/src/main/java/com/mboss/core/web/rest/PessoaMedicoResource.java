package com.mboss.core.web.rest;

import com.mboss.core.domain.PessoaMedico;
import com.mboss.core.repository.PessoaMedicoRepository;
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
 * REST controller for managing {@link com.mboss.core.domain.PessoaMedico}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PessoaMedicoResource {

    private final Logger log = LoggerFactory.getLogger(PessoaMedicoResource.class);

    private static final String ENTITY_NAME = "corePessoaMedico";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PessoaMedicoRepository pessoaMedicoRepository;

    public PessoaMedicoResource(PessoaMedicoRepository pessoaMedicoRepository) {
        this.pessoaMedicoRepository = pessoaMedicoRepository;
    }

    /**
     * {@code POST  /pessoa-medicos} : Create a new pessoaMedico.
     *
     * @param pessoaMedico the pessoaMedico to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pessoaMedico, or with status {@code 400 (Bad Request)} if the pessoaMedico has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pessoa-medicos")
    public Mono<ResponseEntity<PessoaMedico>> createPessoaMedico(@Valid @RequestBody PessoaMedico pessoaMedico) throws URISyntaxException {
        log.debug("REST request to save PessoaMedico : {}", pessoaMedico);
        if (pessoaMedico.getId() != null) {
            throw new BadRequestAlertException("A new pessoaMedico cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return pessoaMedicoRepository
            .save(pessoaMedico)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/pessoa-medicos/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /pessoa-medicos/:id} : Updates an existing pessoaMedico.
     *
     * @param id the id of the pessoaMedico to save.
     * @param pessoaMedico the pessoaMedico to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaMedico,
     * or with status {@code 400 (Bad Request)} if the pessoaMedico is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pessoaMedico couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pessoa-medicos/{id}")
    public Mono<ResponseEntity<PessoaMedico>> updatePessoaMedico(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PessoaMedico pessoaMedico
    ) throws URISyntaxException {
        log.debug("REST request to update PessoaMedico : {}, {}", id, pessoaMedico);
        if (pessoaMedico.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaMedico.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaMedicoRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return pessoaMedicoRepository
                        .save(pessoaMedico)
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
     * {@code PATCH  /pessoa-medicos/:id} : Partial updates given fields of an existing pessoaMedico, field will ignore if it is null
     *
     * @param id the id of the pessoaMedico to save.
     * @param pessoaMedico the pessoaMedico to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaMedico,
     * or with status {@code 400 (Bad Request)} if the pessoaMedico is not valid,
     * or with status {@code 404 (Not Found)} if the pessoaMedico is not found,
     * or with status {@code 500 (Internal Server Error)} if the pessoaMedico couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pessoa-medicos/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<PessoaMedico>> partialUpdatePessoaMedico(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PessoaMedico pessoaMedico
    ) throws URISyntaxException {
        log.debug("REST request to partial update PessoaMedico partially : {}, {}", id, pessoaMedico);
        if (pessoaMedico.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaMedico.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaMedicoRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<PessoaMedico> result = pessoaMedicoRepository
                        .findById(pessoaMedico.getId())
                        .map(
                            existingPessoaMedico -> {
                                if (pessoaMedico.getCrm() != null) {
                                    existingPessoaMedico.setCrm(pessoaMedico.getCrm());
                                }

                                return existingPessoaMedico;
                            }
                        )
                        .flatMap(pessoaMedicoRepository::save);

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
     * {@code GET  /pessoa-medicos} : get all the pessoaMedicos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pessoaMedicos in body.
     */
    @GetMapping("/pessoa-medicos")
    public Mono<List<PessoaMedico>> getAllPessoaMedicos() {
        log.debug("REST request to get all PessoaMedicos");
        return pessoaMedicoRepository.findAll().collectList();
    }

    /**
     * {@code GET  /pessoa-medicos} : get all the pessoaMedicos as a stream.
     * @return the {@link Flux} of pessoaMedicos.
     */
    @GetMapping(value = "/pessoa-medicos", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PessoaMedico> getAllPessoaMedicosAsStream() {
        log.debug("REST request to get all PessoaMedicos as a stream");
        return pessoaMedicoRepository.findAll();
    }

    /**
     * {@code GET  /pessoa-medicos/:id} : get the "id" pessoaMedico.
     *
     * @param id the id of the pessoaMedico to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pessoaMedico, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pessoa-medicos/{id}")
    public Mono<ResponseEntity<PessoaMedico>> getPessoaMedico(@PathVariable Long id) {
        log.debug("REST request to get PessoaMedico : {}", id);
        Mono<PessoaMedico> pessoaMedico = pessoaMedicoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pessoaMedico);
    }

    /**
     * {@code DELETE  /pessoa-medicos/:id} : delete the "id" pessoaMedico.
     *
     * @param id the id of the pessoaMedico to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pessoa-medicos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePessoaMedico(@PathVariable Long id) {
        log.debug("REST request to delete PessoaMedico : {}", id);
        return pessoaMedicoRepository
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
