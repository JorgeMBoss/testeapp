package com.mboss.core.web.rest;

import com.mboss.core.domain.PessoaEndereco;
import com.mboss.core.repository.PessoaEnderecoRepository;
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
 * REST controller for managing {@link com.mboss.core.domain.PessoaEndereco}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PessoaEnderecoResource {

    private final Logger log = LoggerFactory.getLogger(PessoaEnderecoResource.class);

    private static final String ENTITY_NAME = "corePessoaEndereco";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PessoaEnderecoRepository pessoaEnderecoRepository;

    public PessoaEnderecoResource(PessoaEnderecoRepository pessoaEnderecoRepository) {
        this.pessoaEnderecoRepository = pessoaEnderecoRepository;
    }

    /**
     * {@code POST  /pessoa-enderecos} : Create a new pessoaEndereco.
     *
     * @param pessoaEndereco the pessoaEndereco to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pessoaEndereco, or with status {@code 400 (Bad Request)} if the pessoaEndereco has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pessoa-enderecos")
    public Mono<ResponseEntity<PessoaEndereco>> createPessoaEndereco(@Valid @RequestBody PessoaEndereco pessoaEndereco)
        throws URISyntaxException {
        log.debug("REST request to save PessoaEndereco : {}", pessoaEndereco);
        if (pessoaEndereco.getId() != null) {
            throw new BadRequestAlertException("A new pessoaEndereco cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return pessoaEnderecoRepository
            .save(pessoaEndereco)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/pessoa-enderecos/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /pessoa-enderecos/:id} : Updates an existing pessoaEndereco.
     *
     * @param id the id of the pessoaEndereco to save.
     * @param pessoaEndereco the pessoaEndereco to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaEndereco,
     * or with status {@code 400 (Bad Request)} if the pessoaEndereco is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pessoaEndereco couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pessoa-enderecos/{id}")
    public Mono<ResponseEntity<PessoaEndereco>> updatePessoaEndereco(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PessoaEndereco pessoaEndereco
    ) throws URISyntaxException {
        log.debug("REST request to update PessoaEndereco : {}, {}", id, pessoaEndereco);
        if (pessoaEndereco.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaEndereco.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaEnderecoRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return pessoaEnderecoRepository
                        .save(pessoaEndereco)
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
     * {@code PATCH  /pessoa-enderecos/:id} : Partial updates given fields of an existing pessoaEndereco, field will ignore if it is null
     *
     * @param id the id of the pessoaEndereco to save.
     * @param pessoaEndereco the pessoaEndereco to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaEndereco,
     * or with status {@code 400 (Bad Request)} if the pessoaEndereco is not valid,
     * or with status {@code 404 (Not Found)} if the pessoaEndereco is not found,
     * or with status {@code 500 (Internal Server Error)} if the pessoaEndereco couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pessoa-enderecos/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<PessoaEndereco>> partialUpdatePessoaEndereco(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PessoaEndereco pessoaEndereco
    ) throws URISyntaxException {
        log.debug("REST request to partial update PessoaEndereco partially : {}, {}", id, pessoaEndereco);
        if (pessoaEndereco.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaEndereco.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaEnderecoRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<PessoaEndereco> result = pessoaEnderecoRepository
                        .findById(pessoaEndereco.getId())
                        .map(
                            existingPessoaEndereco -> {
                                if (pessoaEndereco.getPais() != null) {
                                    existingPessoaEndereco.setPais(pessoaEndereco.getPais());
                                }
                                if (pessoaEndereco.getEstado() != null) {
                                    existingPessoaEndereco.setEstado(pessoaEndereco.getEstado());
                                }
                                if (pessoaEndereco.getCidade() != null) {
                                    existingPessoaEndereco.setCidade(pessoaEndereco.getCidade());
                                }
                                if (pessoaEndereco.getBairro() != null) {
                                    existingPessoaEndereco.setBairro(pessoaEndereco.getBairro());
                                }
                                if (pessoaEndereco.getNumeroResidencia() != null) {
                                    existingPessoaEndereco.setNumeroResidencia(pessoaEndereco.getNumeroResidencia());
                                }
                                if (pessoaEndereco.getLogradouro() != null) {
                                    existingPessoaEndereco.setLogradouro(pessoaEndereco.getLogradouro());
                                }
                                if (pessoaEndereco.getComplemento() != null) {
                                    existingPessoaEndereco.setComplemento(pessoaEndereco.getComplemento());
                                }

                                return existingPessoaEndereco;
                            }
                        )
                        .flatMap(pessoaEnderecoRepository::save);

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
     * {@code GET  /pessoa-enderecos} : get all the pessoaEnderecos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pessoaEnderecos in body.
     */
    @GetMapping("/pessoa-enderecos")
    public Mono<List<PessoaEndereco>> getAllPessoaEnderecos() {
        log.debug("REST request to get all PessoaEnderecos");
        return pessoaEnderecoRepository.findAll().collectList();
    }

    /**
     * {@code GET  /pessoa-enderecos} : get all the pessoaEnderecos as a stream.
     * @return the {@link Flux} of pessoaEnderecos.
     */
    @GetMapping(value = "/pessoa-enderecos", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PessoaEndereco> getAllPessoaEnderecosAsStream() {
        log.debug("REST request to get all PessoaEnderecos as a stream");
        return pessoaEnderecoRepository.findAll();
    }

    /**
     * {@code GET  /pessoa-enderecos/:id} : get the "id" pessoaEndereco.
     *
     * @param id the id of the pessoaEndereco to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pessoaEndereco, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pessoa-enderecos/{id}")
    public Mono<ResponseEntity<PessoaEndereco>> getPessoaEndereco(@PathVariable Long id) {
        log.debug("REST request to get PessoaEndereco : {}", id);
        Mono<PessoaEndereco> pessoaEndereco = pessoaEnderecoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pessoaEndereco);
    }

    /**
     * {@code DELETE  /pessoa-enderecos/:id} : delete the "id" pessoaEndereco.
     *
     * @param id the id of the pessoaEndereco to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pessoa-enderecos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePessoaEndereco(@PathVariable Long id) {
        log.debug("REST request to delete PessoaEndereco : {}", id);
        return pessoaEnderecoRepository
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
