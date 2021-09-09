package com.mboss.core.web.rest;

import com.mboss.core.domain.PessoaComplemento;
import com.mboss.core.repository.PessoaComplementoRepository;
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
 * REST controller for managing {@link com.mboss.core.domain.PessoaComplemento}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PessoaComplementoResource {

    private final Logger log = LoggerFactory.getLogger(PessoaComplementoResource.class);

    private static final String ENTITY_NAME = "corePessoaComplemento";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PessoaComplementoRepository pessoaComplementoRepository;

    public PessoaComplementoResource(PessoaComplementoRepository pessoaComplementoRepository) {
        this.pessoaComplementoRepository = pessoaComplementoRepository;
    }

    /**
     * {@code POST  /pessoa-complementos} : Create a new pessoaComplemento.
     *
     * @param pessoaComplemento the pessoaComplemento to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pessoaComplemento, or with status {@code 400 (Bad Request)} if the pessoaComplemento has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pessoa-complementos")
    public Mono<ResponseEntity<PessoaComplemento>> createPessoaComplemento(@Valid @RequestBody PessoaComplemento pessoaComplemento)
        throws URISyntaxException {
        log.debug("REST request to save PessoaComplemento : {}", pessoaComplemento);
        if (pessoaComplemento.getId() != null) {
            throw new BadRequestAlertException("A new pessoaComplemento cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return pessoaComplementoRepository
            .save(pessoaComplemento)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/pessoa-complementos/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /pessoa-complementos/:id} : Updates an existing pessoaComplemento.
     *
     * @param id the id of the pessoaComplemento to save.
     * @param pessoaComplemento the pessoaComplemento to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaComplemento,
     * or with status {@code 400 (Bad Request)} if the pessoaComplemento is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pessoaComplemento couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pessoa-complementos/{id}")
    public Mono<ResponseEntity<PessoaComplemento>> updatePessoaComplemento(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PessoaComplemento pessoaComplemento
    ) throws URISyntaxException {
        log.debug("REST request to update PessoaComplemento : {}, {}", id, pessoaComplemento);
        if (pessoaComplemento.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaComplemento.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaComplementoRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return pessoaComplementoRepository
                        .save(pessoaComplemento)
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
     * {@code PATCH  /pessoa-complementos/:id} : Partial updates given fields of an existing pessoaComplemento, field will ignore if it is null
     *
     * @param id the id of the pessoaComplemento to save.
     * @param pessoaComplemento the pessoaComplemento to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaComplemento,
     * or with status {@code 400 (Bad Request)} if the pessoaComplemento is not valid,
     * or with status {@code 404 (Not Found)} if the pessoaComplemento is not found,
     * or with status {@code 500 (Internal Server Error)} if the pessoaComplemento couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pessoa-complementos/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<PessoaComplemento>> partialUpdatePessoaComplemento(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PessoaComplemento pessoaComplemento
    ) throws URISyntaxException {
        log.debug("REST request to partial update PessoaComplemento partially : {}, {}", id, pessoaComplemento);
        if (pessoaComplemento.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaComplemento.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaComplementoRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<PessoaComplemento> result = pessoaComplementoRepository
                        .findById(pessoaComplemento.getId())
                        .map(
                            existingPessoaComplemento -> {
                                if (pessoaComplemento.getIe() != null) {
                                    existingPessoaComplemento.setIe(pessoaComplemento.getIe());
                                }
                                if (pessoaComplemento.getIm() != null) {
                                    existingPessoaComplemento.setIm(pessoaComplemento.getIm());
                                }
                                if (pessoaComplemento.getEmail() != null) {
                                    existingPessoaComplemento.setEmail(pessoaComplemento.getEmail());
                                }

                                return existingPessoaComplemento;
                            }
                        )
                        .flatMap(pessoaComplementoRepository::save);

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
     * {@code GET  /pessoa-complementos} : get all the pessoaComplementos.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pessoaComplementos in body.
     */
    @GetMapping("/pessoa-complementos")
    public Mono<List<PessoaComplemento>> getAllPessoaComplementos(@RequestParam(required = false) String filter) {
        if ("pessoafisica-is-null".equals(filter)) {
            log.debug("REST request to get all PessoaComplementos where pessoaFisica is null");
            return pessoaComplementoRepository.findAllWherePessoaFisicaIsNull().collectList();
        }

        if ("pessoajuridica-is-null".equals(filter)) {
            log.debug("REST request to get all PessoaComplementos where pessoaJuridica is null");
            return pessoaComplementoRepository.findAllWherePessoaJuridicaIsNull().collectList();
        }
        log.debug("REST request to get all PessoaComplementos");
        return pessoaComplementoRepository.findAll().collectList();
    }

    /**
     * {@code GET  /pessoa-complementos} : get all the pessoaComplementos as a stream.
     * @return the {@link Flux} of pessoaComplementos.
     */
    @GetMapping(value = "/pessoa-complementos", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PessoaComplemento> getAllPessoaComplementosAsStream() {
        log.debug("REST request to get all PessoaComplementos as a stream");
        return pessoaComplementoRepository.findAll();
    }

    /**
     * {@code GET  /pessoa-complementos/:id} : get the "id" pessoaComplemento.
     *
     * @param id the id of the pessoaComplemento to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pessoaComplemento, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pessoa-complementos/{id}")
    public Mono<ResponseEntity<PessoaComplemento>> getPessoaComplemento(@PathVariable Long id) {
        log.debug("REST request to get PessoaComplemento : {}", id);
        Mono<PessoaComplemento> pessoaComplemento = pessoaComplementoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pessoaComplemento);
    }

    /**
     * {@code DELETE  /pessoa-complementos/:id} : delete the "id" pessoaComplemento.
     *
     * @param id the id of the pessoaComplemento to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pessoa-complementos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePessoaComplemento(@PathVariable Long id) {
        log.debug("REST request to delete PessoaComplemento : {}", id);
        return pessoaComplementoRepository
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
