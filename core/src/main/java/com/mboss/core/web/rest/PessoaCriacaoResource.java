package com.mboss.core.web.rest;

import com.mboss.core.domain.PessoaCriacao;
import com.mboss.core.repository.PessoaCriacaoRepository;
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
 * REST controller for managing {@link com.mboss.core.domain.PessoaCriacao}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PessoaCriacaoResource {

    private final Logger log = LoggerFactory.getLogger(PessoaCriacaoResource.class);

    private static final String ENTITY_NAME = "corePessoaCriacao";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PessoaCriacaoRepository pessoaCriacaoRepository;

    public PessoaCriacaoResource(PessoaCriacaoRepository pessoaCriacaoRepository) {
        this.pessoaCriacaoRepository = pessoaCriacaoRepository;
    }

    /**
     * {@code POST  /pessoa-criacaos} : Create a new pessoaCriacao.
     *
     * @param pessoaCriacao the pessoaCriacao to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pessoaCriacao, or with status {@code 400 (Bad Request)} if the pessoaCriacao has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pessoa-criacaos")
    public Mono<ResponseEntity<PessoaCriacao>> createPessoaCriacao(@RequestBody PessoaCriacao pessoaCriacao) throws URISyntaxException {
        log.debug("REST request to save PessoaCriacao : {}", pessoaCriacao);
        if (pessoaCriacao.getId() != null) {
            throw new BadRequestAlertException("A new pessoaCriacao cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return pessoaCriacaoRepository
            .save(pessoaCriacao)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/pessoa-criacaos/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /pessoa-criacaos/:id} : Updates an existing pessoaCriacao.
     *
     * @param id the id of the pessoaCriacao to save.
     * @param pessoaCriacao the pessoaCriacao to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaCriacao,
     * or with status {@code 400 (Bad Request)} if the pessoaCriacao is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pessoaCriacao couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pessoa-criacaos/{id}")
    public Mono<ResponseEntity<PessoaCriacao>> updatePessoaCriacao(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PessoaCriacao pessoaCriacao
    ) throws URISyntaxException {
        log.debug("REST request to update PessoaCriacao : {}, {}", id, pessoaCriacao);
        if (pessoaCriacao.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaCriacao.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaCriacaoRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return pessoaCriacaoRepository
                        .save(pessoaCriacao)
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
     * {@code PATCH  /pessoa-criacaos/:id} : Partial updates given fields of an existing pessoaCriacao, field will ignore if it is null
     *
     * @param id the id of the pessoaCriacao to save.
     * @param pessoaCriacao the pessoaCriacao to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaCriacao,
     * or with status {@code 400 (Bad Request)} if the pessoaCriacao is not valid,
     * or with status {@code 404 (Not Found)} if the pessoaCriacao is not found,
     * or with status {@code 500 (Internal Server Error)} if the pessoaCriacao couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pessoa-criacaos/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<PessoaCriacao>> partialUpdatePessoaCriacao(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PessoaCriacao pessoaCriacao
    ) throws URISyntaxException {
        log.debug("REST request to partial update PessoaCriacao partially : {}, {}", id, pessoaCriacao);
        if (pessoaCriacao.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaCriacao.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaCriacaoRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<PessoaCriacao> result = pessoaCriacaoRepository
                        .findById(pessoaCriacao.getId())
                        .map(
                            existingPessoaCriacao -> {
                                if (pessoaCriacao.getIdPessoa() != null) {
                                    existingPessoaCriacao.setIdPessoa(pessoaCriacao.getIdPessoa());
                                }
                                if (pessoaCriacao.getIdCriacao() != null) {
                                    existingPessoaCriacao.setIdCriacao(pessoaCriacao.getIdCriacao());
                                }

                                return existingPessoaCriacao;
                            }
                        )
                        .flatMap(pessoaCriacaoRepository::save);

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
     * {@code GET  /pessoa-criacaos} : get all the pessoaCriacaos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pessoaCriacaos in body.
     */
    @GetMapping("/pessoa-criacaos")
    public Mono<List<PessoaCriacao>> getAllPessoaCriacaos() {
        log.debug("REST request to get all PessoaCriacaos");
        return pessoaCriacaoRepository.findAll().collectList();
    }

    /**
     * {@code GET  /pessoa-criacaos} : get all the pessoaCriacaos as a stream.
     * @return the {@link Flux} of pessoaCriacaos.
     */
    @GetMapping(value = "/pessoa-criacaos", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PessoaCriacao> getAllPessoaCriacaosAsStream() {
        log.debug("REST request to get all PessoaCriacaos as a stream");
        return pessoaCriacaoRepository.findAll();
    }

    /**
     * {@code GET  /pessoa-criacaos/:id} : get the "id" pessoaCriacao.
     *
     * @param id the id of the pessoaCriacao to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pessoaCriacao, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pessoa-criacaos/{id}")
    public Mono<ResponseEntity<PessoaCriacao>> getPessoaCriacao(@PathVariable Long id) {
        log.debug("REST request to get PessoaCriacao : {}", id);
        Mono<PessoaCriacao> pessoaCriacao = pessoaCriacaoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pessoaCriacao);
    }

    /**
     * {@code DELETE  /pessoa-criacaos/:id} : delete the "id" pessoaCriacao.
     *
     * @param id the id of the pessoaCriacao to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pessoa-criacaos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePessoaCriacao(@PathVariable Long id) {
        log.debug("REST request to delete PessoaCriacao : {}", id);
        return pessoaCriacaoRepository
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
