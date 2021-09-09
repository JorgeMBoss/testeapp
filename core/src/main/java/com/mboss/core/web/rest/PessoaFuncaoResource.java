package com.mboss.core.web.rest;

import com.mboss.core.domain.PessoaFuncao;
import com.mboss.core.repository.PessoaFuncaoRepository;
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
 * REST controller for managing {@link com.mboss.core.domain.PessoaFuncao}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PessoaFuncaoResource {

    private final Logger log = LoggerFactory.getLogger(PessoaFuncaoResource.class);

    private static final String ENTITY_NAME = "corePessoaFuncao";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PessoaFuncaoRepository pessoaFuncaoRepository;

    public PessoaFuncaoResource(PessoaFuncaoRepository pessoaFuncaoRepository) {
        this.pessoaFuncaoRepository = pessoaFuncaoRepository;
    }

    /**
     * {@code POST  /pessoa-funcaos} : Create a new pessoaFuncao.
     *
     * @param pessoaFuncao the pessoaFuncao to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pessoaFuncao, or with status {@code 400 (Bad Request)} if the pessoaFuncao has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pessoa-funcaos")
    public Mono<ResponseEntity<PessoaFuncao>> createPessoaFuncao(@Valid @RequestBody PessoaFuncao pessoaFuncao) throws URISyntaxException {
        log.debug("REST request to save PessoaFuncao : {}", pessoaFuncao);
        if (pessoaFuncao.getId() != null) {
            throw new BadRequestAlertException("A new pessoaFuncao cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return pessoaFuncaoRepository
            .save(pessoaFuncao)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/pessoa-funcaos/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /pessoa-funcaos/:id} : Updates an existing pessoaFuncao.
     *
     * @param id the id of the pessoaFuncao to save.
     * @param pessoaFuncao the pessoaFuncao to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaFuncao,
     * or with status {@code 400 (Bad Request)} if the pessoaFuncao is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pessoaFuncao couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pessoa-funcaos/{id}")
    public Mono<ResponseEntity<PessoaFuncao>> updatePessoaFuncao(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PessoaFuncao pessoaFuncao
    ) throws URISyntaxException {
        log.debug("REST request to update PessoaFuncao : {}, {}", id, pessoaFuncao);
        if (pessoaFuncao.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaFuncao.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaFuncaoRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return pessoaFuncaoRepository
                        .save(pessoaFuncao)
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
     * {@code PATCH  /pessoa-funcaos/:id} : Partial updates given fields of an existing pessoaFuncao, field will ignore if it is null
     *
     * @param id the id of the pessoaFuncao to save.
     * @param pessoaFuncao the pessoaFuncao to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaFuncao,
     * or with status {@code 400 (Bad Request)} if the pessoaFuncao is not valid,
     * or with status {@code 404 (Not Found)} if the pessoaFuncao is not found,
     * or with status {@code 500 (Internal Server Error)} if the pessoaFuncao couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pessoa-funcaos/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<PessoaFuncao>> partialUpdatePessoaFuncao(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PessoaFuncao pessoaFuncao
    ) throws URISyntaxException {
        log.debug("REST request to partial update PessoaFuncao partially : {}, {}", id, pessoaFuncao);
        if (pessoaFuncao.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaFuncao.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaFuncaoRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<PessoaFuncao> result = pessoaFuncaoRepository
                        .findById(pessoaFuncao.getId())
                        .map(
                            existingPessoaFuncao -> {
                                if (pessoaFuncao.getDescricao() != null) {
                                    existingPessoaFuncao.setDescricao(pessoaFuncao.getDescricao());
                                }
                                if (pessoaFuncao.getAtivo() != null) {
                                    existingPessoaFuncao.setAtivo(pessoaFuncao.getAtivo());
                                }

                                return existingPessoaFuncao;
                            }
                        )
                        .flatMap(pessoaFuncaoRepository::save);

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
     * {@code GET  /pessoa-funcaos} : get all the pessoaFuncaos.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pessoaFuncaos in body.
     */
    @GetMapping("/pessoa-funcaos")
    public Mono<List<PessoaFuncao>> getAllPessoaFuncaos(@RequestParam(required = false) String filter) {
        if ("pessoacolaborador-is-null".equals(filter)) {
            log.debug("REST request to get all PessoaFuncaos where pessoaColaborador is null");
            return pessoaFuncaoRepository.findAllWherePessoaColaboradorIsNull().collectList();
        }
        log.debug("REST request to get all PessoaFuncaos");
        return pessoaFuncaoRepository.findAll().collectList();
    }

    /**
     * {@code GET  /pessoa-funcaos} : get all the pessoaFuncaos as a stream.
     * @return the {@link Flux} of pessoaFuncaos.
     */
    @GetMapping(value = "/pessoa-funcaos", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PessoaFuncao> getAllPessoaFuncaosAsStream() {
        log.debug("REST request to get all PessoaFuncaos as a stream");
        return pessoaFuncaoRepository.findAll();
    }

    /**
     * {@code GET  /pessoa-funcaos/:id} : get the "id" pessoaFuncao.
     *
     * @param id the id of the pessoaFuncao to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pessoaFuncao, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pessoa-funcaos/{id}")
    public Mono<ResponseEntity<PessoaFuncao>> getPessoaFuncao(@PathVariable Long id) {
        log.debug("REST request to get PessoaFuncao : {}", id);
        Mono<PessoaFuncao> pessoaFuncao = pessoaFuncaoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pessoaFuncao);
    }

    /**
     * {@code DELETE  /pessoa-funcaos/:id} : delete the "id" pessoaFuncao.
     *
     * @param id the id of the pessoaFuncao to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pessoa-funcaos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePessoaFuncao(@PathVariable Long id) {
        log.debug("REST request to delete PessoaFuncao : {}", id);
        return pessoaFuncaoRepository
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
