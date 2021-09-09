package com.mboss.core.web.rest;

import com.mboss.core.domain.PessoaJuridica;
import com.mboss.core.repository.PessoaJuridicaRepository;
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
 * REST controller for managing {@link com.mboss.core.domain.PessoaJuridica}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PessoaJuridicaResource {

    private final Logger log = LoggerFactory.getLogger(PessoaJuridicaResource.class);

    private static final String ENTITY_NAME = "corePessoaJuridica";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PessoaJuridicaRepository pessoaJuridicaRepository;

    public PessoaJuridicaResource(PessoaJuridicaRepository pessoaJuridicaRepository) {
        this.pessoaJuridicaRepository = pessoaJuridicaRepository;
    }

    /**
     * {@code POST  /pessoa-juridicas} : Create a new pessoaJuridica.
     *
     * @param pessoaJuridica the pessoaJuridica to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pessoaJuridica, or with status {@code 400 (Bad Request)} if the pessoaJuridica has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pessoa-juridicas")
    public Mono<ResponseEntity<PessoaJuridica>> createPessoaJuridica(@Valid @RequestBody PessoaJuridica pessoaJuridica)
        throws URISyntaxException {
        log.debug("REST request to save PessoaJuridica : {}", pessoaJuridica);
        if (pessoaJuridica.getId() != null) {
            throw new BadRequestAlertException("A new pessoaJuridica cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return pessoaJuridicaRepository
            .save(pessoaJuridica)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/pessoa-juridicas/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /pessoa-juridicas/:id} : Updates an existing pessoaJuridica.
     *
     * @param id the id of the pessoaJuridica to save.
     * @param pessoaJuridica the pessoaJuridica to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaJuridica,
     * or with status {@code 400 (Bad Request)} if the pessoaJuridica is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pessoaJuridica couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pessoa-juridicas/{id}")
    public Mono<ResponseEntity<PessoaJuridica>> updatePessoaJuridica(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PessoaJuridica pessoaJuridica
    ) throws URISyntaxException {
        log.debug("REST request to update PessoaJuridica : {}, {}", id, pessoaJuridica);
        if (pessoaJuridica.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaJuridica.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaJuridicaRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return pessoaJuridicaRepository
                        .save(pessoaJuridica)
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
     * {@code PATCH  /pessoa-juridicas/:id} : Partial updates given fields of an existing pessoaJuridica, field will ignore if it is null
     *
     * @param id the id of the pessoaJuridica to save.
     * @param pessoaJuridica the pessoaJuridica to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaJuridica,
     * or with status {@code 400 (Bad Request)} if the pessoaJuridica is not valid,
     * or with status {@code 404 (Not Found)} if the pessoaJuridica is not found,
     * or with status {@code 500 (Internal Server Error)} if the pessoaJuridica couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pessoa-juridicas/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<PessoaJuridica>> partialUpdatePessoaJuridica(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PessoaJuridica pessoaJuridica
    ) throws URISyntaxException {
        log.debug("REST request to partial update PessoaJuridica partially : {}, {}", id, pessoaJuridica);
        if (pessoaJuridica.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaJuridica.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaJuridicaRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<PessoaJuridica> result = pessoaJuridicaRepository
                        .findById(pessoaJuridica.getId())
                        .map(
                            existingPessoaJuridica -> {
                                if (pessoaJuridica.getCnpj() != null) {
                                    existingPessoaJuridica.setCnpj(pessoaJuridica.getCnpj());
                                }
                                if (pessoaJuridica.getNomeRazao() != null) {
                                    existingPessoaJuridica.setNomeRazao(pessoaJuridica.getNomeRazao());
                                }
                                if (pessoaJuridica.getNomeFantasia() != null) {
                                    existingPessoaJuridica.setNomeFantasia(pessoaJuridica.getNomeFantasia());
                                }

                                return existingPessoaJuridica;
                            }
                        )
                        .flatMap(pessoaJuridicaRepository::save);

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
     * {@code GET  /pessoa-juridicas} : get all the pessoaJuridicas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pessoaJuridicas in body.
     */
    @GetMapping("/pessoa-juridicas")
    public Mono<List<PessoaJuridica>> getAllPessoaJuridicas() {
        log.debug("REST request to get all PessoaJuridicas");
        return pessoaJuridicaRepository.findAll().collectList();
    }

    /**
     * {@code GET  /pessoa-juridicas} : get all the pessoaJuridicas as a stream.
     * @return the {@link Flux} of pessoaJuridicas.
     */
    @GetMapping(value = "/pessoa-juridicas", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PessoaJuridica> getAllPessoaJuridicasAsStream() {
        log.debug("REST request to get all PessoaJuridicas as a stream");
        return pessoaJuridicaRepository.findAll();
    }

    /**
     * {@code GET  /pessoa-juridicas/:id} : get the "id" pessoaJuridica.
     *
     * @param id the id of the pessoaJuridica to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pessoaJuridica, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pessoa-juridicas/{id}")
    public Mono<ResponseEntity<PessoaJuridica>> getPessoaJuridica(@PathVariable Long id) {
        log.debug("REST request to get PessoaJuridica : {}", id);
        Mono<PessoaJuridica> pessoaJuridica = pessoaJuridicaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pessoaJuridica);
    }

    /**
     * {@code DELETE  /pessoa-juridicas/:id} : delete the "id" pessoaJuridica.
     *
     * @param id the id of the pessoaJuridica to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pessoa-juridicas/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePessoaJuridica(@PathVariable Long id) {
        log.debug("REST request to delete PessoaJuridica : {}", id);
        return pessoaJuridicaRepository
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
