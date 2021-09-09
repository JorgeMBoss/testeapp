package com.mboss.core.web.rest;

import com.mboss.core.domain.PessoaFisca;
import com.mboss.core.repository.PessoaFiscaRepository;
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
 * REST controller for managing {@link com.mboss.core.domain.PessoaFisca}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PessoaFiscaResource {

    private final Logger log = LoggerFactory.getLogger(PessoaFiscaResource.class);

    private static final String ENTITY_NAME = "corePessoaFisca";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PessoaFiscaRepository pessoaFiscaRepository;

    public PessoaFiscaResource(PessoaFiscaRepository pessoaFiscaRepository) {
        this.pessoaFiscaRepository = pessoaFiscaRepository;
    }

    /**
     * {@code POST  /pessoa-fiscas} : Create a new pessoaFisca.
     *
     * @param pessoaFisca the pessoaFisca to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pessoaFisca, or with status {@code 400 (Bad Request)} if the pessoaFisca has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pessoa-fiscas")
    public Mono<ResponseEntity<PessoaFisca>> createPessoaFisca(@Valid @RequestBody PessoaFisca pessoaFisca) throws URISyntaxException {
        log.debug("REST request to save PessoaFisca : {}", pessoaFisca);
        if (pessoaFisca.getId() != null) {
            throw new BadRequestAlertException("A new pessoaFisca cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return pessoaFiscaRepository
            .save(pessoaFisca)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/pessoa-fiscas/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /pessoa-fiscas/:id} : Updates an existing pessoaFisca.
     *
     * @param id the id of the pessoaFisca to save.
     * @param pessoaFisca the pessoaFisca to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaFisca,
     * or with status {@code 400 (Bad Request)} if the pessoaFisca is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pessoaFisca couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pessoa-fiscas/{id}")
    public Mono<ResponseEntity<PessoaFisca>> updatePessoaFisca(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PessoaFisca pessoaFisca
    ) throws URISyntaxException {
        log.debug("REST request to update PessoaFisca : {}, {}", id, pessoaFisca);
        if (pessoaFisca.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaFisca.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaFiscaRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return pessoaFiscaRepository
                        .save(pessoaFisca)
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
     * {@code PATCH  /pessoa-fiscas/:id} : Partial updates given fields of an existing pessoaFisca, field will ignore if it is null
     *
     * @param id the id of the pessoaFisca to save.
     * @param pessoaFisca the pessoaFisca to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaFisca,
     * or with status {@code 400 (Bad Request)} if the pessoaFisca is not valid,
     * or with status {@code 404 (Not Found)} if the pessoaFisca is not found,
     * or with status {@code 500 (Internal Server Error)} if the pessoaFisca couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pessoa-fiscas/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<PessoaFisca>> partialUpdatePessoaFisca(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PessoaFisca pessoaFisca
    ) throws URISyntaxException {
        log.debug("REST request to partial update PessoaFisca partially : {}, {}", id, pessoaFisca);
        if (pessoaFisca.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaFisca.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaFiscaRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<PessoaFisca> result = pessoaFiscaRepository
                        .findById(pessoaFisca.getId())
                        .map(
                            existingPessoaFisca -> {
                                if (pessoaFisca.getCpf() != null) {
                                    existingPessoaFisca.setCpf(pessoaFisca.getCpf());
                                }
                                if (pessoaFisca.getRg() != null) {
                                    existingPessoaFisca.setRg(pessoaFisca.getRg());
                                }
                                if (pessoaFisca.getDataNascimento() != null) {
                                    existingPessoaFisca.setDataNascimento(pessoaFisca.getDataNascimento());
                                }
                                if (pessoaFisca.getIdade() != null) {
                                    existingPessoaFisca.setIdade(pessoaFisca.getIdade());
                                }
                                if (pessoaFisca.getSexo() != null) {
                                    existingPessoaFisca.setSexo(pessoaFisca.getSexo());
                                }
                                if (pessoaFisca.getCor() != null) {
                                    existingPessoaFisca.setCor(pessoaFisca.getCor());
                                }
                                if (pessoaFisca.getEstadoCivil() != null) {
                                    existingPessoaFisca.setEstadoCivil(pessoaFisca.getEstadoCivil());
                                }
                                if (pessoaFisca.getNaturalidade() != null) {
                                    existingPessoaFisca.setNaturalidade(pessoaFisca.getNaturalidade());
                                }
                                if (pessoaFisca.getNacionalidade() != null) {
                                    existingPessoaFisca.setNacionalidade(pessoaFisca.getNacionalidade());
                                }

                                return existingPessoaFisca;
                            }
                        )
                        .flatMap(pessoaFiscaRepository::save);

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
     * {@code GET  /pessoa-fiscas} : get all the pessoaFiscas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pessoaFiscas in body.
     */
    @GetMapping("/pessoa-fiscas")
    public Mono<List<PessoaFisca>> getAllPessoaFiscas() {
        log.debug("REST request to get all PessoaFiscas");
        return pessoaFiscaRepository.findAll().collectList();
    }

    /**
     * {@code GET  /pessoa-fiscas} : get all the pessoaFiscas as a stream.
     * @return the {@link Flux} of pessoaFiscas.
     */
    @GetMapping(value = "/pessoa-fiscas", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PessoaFisca> getAllPessoaFiscasAsStream() {
        log.debug("REST request to get all PessoaFiscas as a stream");
        return pessoaFiscaRepository.findAll();
    }

    /**
     * {@code GET  /pessoa-fiscas/:id} : get the "id" pessoaFisca.
     *
     * @param id the id of the pessoaFisca to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pessoaFisca, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pessoa-fiscas/{id}")
    public Mono<ResponseEntity<PessoaFisca>> getPessoaFisca(@PathVariable Long id) {
        log.debug("REST request to get PessoaFisca : {}", id);
        Mono<PessoaFisca> pessoaFisca = pessoaFiscaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pessoaFisca);
    }

    /**
     * {@code DELETE  /pessoa-fiscas/:id} : delete the "id" pessoaFisca.
     *
     * @param id the id of the pessoaFisca to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pessoa-fiscas/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePessoaFisca(@PathVariable Long id) {
        log.debug("REST request to delete PessoaFisca : {}", id);
        return pessoaFiscaRepository
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
