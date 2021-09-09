package com.mboss.core.web.rest;

import com.mboss.core.domain.PessoaFisica;
import com.mboss.core.repository.PessoaFisicaRepository;
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
 * REST controller for managing {@link com.mboss.core.domain.PessoaFisica}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PessoaFisicaResource {

    private final Logger log = LoggerFactory.getLogger(PessoaFisicaResource.class);

    private static final String ENTITY_NAME = "corePessoaFisica";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PessoaFisicaRepository pessoaFisicaRepository;

    public PessoaFisicaResource(PessoaFisicaRepository pessoaFisicaRepository) {
        this.pessoaFisicaRepository = pessoaFisicaRepository;
    }

    /**
     * {@code POST  /pessoa-fisicas} : Create a new pessoaFisica.
     *
     * @param pessoaFisica the pessoaFisica to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pessoaFisica, or with status {@code 400 (Bad Request)} if the pessoaFisica has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pessoa-fisicas")
    public Mono<ResponseEntity<PessoaFisica>> createPessoaFisica(@Valid @RequestBody PessoaFisica pessoaFisica) throws URISyntaxException {
        log.debug("REST request to save PessoaFisica : {}", pessoaFisica);
        if (pessoaFisica.getId() != null) {
            throw new BadRequestAlertException("A new pessoaFisica cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return pessoaFisicaRepository
            .save(pessoaFisica)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/pessoa-fisicas/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /pessoa-fisicas/:id} : Updates an existing pessoaFisica.
     *
     * @param id the id of the pessoaFisica to save.
     * @param pessoaFisica the pessoaFisica to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaFisica,
     * or with status {@code 400 (Bad Request)} if the pessoaFisica is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pessoaFisica couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pessoa-fisicas/{id}")
    public Mono<ResponseEntity<PessoaFisica>> updatePessoaFisica(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PessoaFisica pessoaFisica
    ) throws URISyntaxException {
        log.debug("REST request to update PessoaFisica : {}, {}", id, pessoaFisica);
        if (pessoaFisica.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaFisica.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaFisicaRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return pessoaFisicaRepository
                        .save(pessoaFisica)
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
     * {@code PATCH  /pessoa-fisicas/:id} : Partial updates given fields of an existing pessoaFisica, field will ignore if it is null
     *
     * @param id the id of the pessoaFisica to save.
     * @param pessoaFisica the pessoaFisica to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaFisica,
     * or with status {@code 400 (Bad Request)} if the pessoaFisica is not valid,
     * or with status {@code 404 (Not Found)} if the pessoaFisica is not found,
     * or with status {@code 500 (Internal Server Error)} if the pessoaFisica couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pessoa-fisicas/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<PessoaFisica>> partialUpdatePessoaFisica(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PessoaFisica pessoaFisica
    ) throws URISyntaxException {
        log.debug("REST request to partial update PessoaFisica partially : {}, {}", id, pessoaFisica);
        if (pessoaFisica.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaFisica.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaFisicaRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<PessoaFisica> result = pessoaFisicaRepository
                        .findById(pessoaFisica.getId())
                        .map(
                            existingPessoaFisica -> {
                                if (pessoaFisica.getCpf() != null) {
                                    existingPessoaFisica.setCpf(pessoaFisica.getCpf());
                                }
                                if (pessoaFisica.getRg() != null) {
                                    existingPessoaFisica.setRg(pessoaFisica.getRg());
                                }
                                if (pessoaFisica.getDataNascimento() != null) {
                                    existingPessoaFisica.setDataNascimento(pessoaFisica.getDataNascimento());
                                }
                                if (pessoaFisica.getIdade() != null) {
                                    existingPessoaFisica.setIdade(pessoaFisica.getIdade());
                                }
                                if (pessoaFisica.getSexo() != null) {
                                    existingPessoaFisica.setSexo(pessoaFisica.getSexo());
                                }
                                if (pessoaFisica.getCor() != null) {
                                    existingPessoaFisica.setCor(pessoaFisica.getCor());
                                }
                                if (pessoaFisica.getEstadoCivil() != null) {
                                    existingPessoaFisica.setEstadoCivil(pessoaFisica.getEstadoCivil());
                                }
                                if (pessoaFisica.getNaturalidade() != null) {
                                    existingPessoaFisica.setNaturalidade(pessoaFisica.getNaturalidade());
                                }
                                if (pessoaFisica.getNacionalidade() != null) {
                                    existingPessoaFisica.setNacionalidade(pessoaFisica.getNacionalidade());
                                }

                                return existingPessoaFisica;
                            }
                        )
                        .flatMap(pessoaFisicaRepository::save);

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
     * {@code GET  /pessoa-fisicas} : get all the pessoaFisicas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pessoaFisicas in body.
     */
    @GetMapping("/pessoa-fisicas")
    public Mono<List<PessoaFisica>> getAllPessoaFisicas() {
        log.debug("REST request to get all PessoaFisicas");
        return pessoaFisicaRepository.findAll().collectList();
    }

    /**
     * {@code GET  /pessoa-fisicas} : get all the pessoaFisicas as a stream.
     * @return the {@link Flux} of pessoaFisicas.
     */
    @GetMapping(value = "/pessoa-fisicas", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PessoaFisica> getAllPessoaFisicasAsStream() {
        log.debug("REST request to get all PessoaFisicas as a stream");
        return pessoaFisicaRepository.findAll();
    }

    /**
     * {@code GET  /pessoa-fisicas/:id} : get the "id" pessoaFisica.
     *
     * @param id the id of the pessoaFisica to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pessoaFisica, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pessoa-fisicas/{id}")
    public Mono<ResponseEntity<PessoaFisica>> getPessoaFisica(@PathVariable Long id) {
        log.debug("REST request to get PessoaFisica : {}", id);
        Mono<PessoaFisica> pessoaFisica = pessoaFisicaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pessoaFisica);
    }

    /**
     * {@code DELETE  /pessoa-fisicas/:id} : delete the "id" pessoaFisica.
     *
     * @param id the id of the pessoaFisica to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pessoa-fisicas/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePessoaFisica(@PathVariable Long id) {
        log.debug("REST request to delete PessoaFisica : {}", id);
        return pessoaFisicaRepository
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
