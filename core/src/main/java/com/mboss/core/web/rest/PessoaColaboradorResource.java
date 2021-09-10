package com.mboss.core.web.rest;

import com.mboss.core.domain.PessoaColaborador;
import com.mboss.core.repository.PessoaColaboradorRepository;
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
 * REST controller for managing {@link com.mboss.core.domain.PessoaColaborador}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PessoaColaboradorResource {

    private final Logger log = LoggerFactory.getLogger(PessoaColaboradorResource.class);

    private static final String ENTITY_NAME = "corePessoaColaborador";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PessoaColaboradorRepository pessoaColaboradorRepository;

    public PessoaColaboradorResource(PessoaColaboradorRepository pessoaColaboradorRepository) {
        this.pessoaColaboradorRepository = pessoaColaboradorRepository;
    }

    /**
     * {@code POST  /pessoa-colaboradors} : Create a new pessoaColaborador.
     *
     * @param pessoaColaborador the pessoaColaborador to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pessoaColaborador, or with status {@code 400 (Bad Request)} if the pessoaColaborador has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pessoa-colaboradors")
    public Mono<ResponseEntity<PessoaColaborador>> createPessoaColaborador(@Valid @RequestBody PessoaColaborador pessoaColaborador)
        throws URISyntaxException {
        log.debug("REST request to save PessoaColaborador : {}", pessoaColaborador);
        if (pessoaColaborador.getId() != null) {
            throw new BadRequestAlertException("A new pessoaColaborador cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return pessoaColaboradorRepository
            .save(pessoaColaborador)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/pessoa-colaboradors/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /pessoa-colaboradors/:id} : Updates an existing pessoaColaborador.
     *
     * @param id the id of the pessoaColaborador to save.
     * @param pessoaColaborador the pessoaColaborador to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaColaborador,
     * or with status {@code 400 (Bad Request)} if the pessoaColaborador is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pessoaColaborador couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pessoa-colaboradors/{id}")
    public Mono<ResponseEntity<PessoaColaborador>> updatePessoaColaborador(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PessoaColaborador pessoaColaborador
    ) throws URISyntaxException {
        log.debug("REST request to update PessoaColaborador : {}, {}", id, pessoaColaborador);
        if (pessoaColaborador.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaColaborador.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaColaboradorRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return pessoaColaboradorRepository
                        .save(pessoaColaborador)
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
     * {@code PATCH  /pessoa-colaboradors/:id} : Partial updates given fields of an existing pessoaColaborador, field will ignore if it is null
     *
     * @param id the id of the pessoaColaborador to save.
     * @param pessoaColaborador the pessoaColaborador to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaColaborador,
     * or with status {@code 400 (Bad Request)} if the pessoaColaborador is not valid,
     * or with status {@code 404 (Not Found)} if the pessoaColaborador is not found,
     * or with status {@code 500 (Internal Server Error)} if the pessoaColaborador couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pessoa-colaboradors/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<PessoaColaborador>> partialUpdatePessoaColaborador(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PessoaColaborador pessoaColaborador
    ) throws URISyntaxException {
        log.debug("REST request to partial update PessoaColaborador partially : {}, {}", id, pessoaColaborador);
        if (pessoaColaborador.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaColaborador.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pessoaColaboradorRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<PessoaColaborador> result = pessoaColaboradorRepository
                        .findById(pessoaColaborador.getId())
                        .map(
                            existingPessoaColaborador -> {
                                if (pessoaColaborador.getDataAdimissao() != null) {
                                    existingPessoaColaborador.setDataAdimissao(pessoaColaborador.getDataAdimissao());
                                }
                                if (pessoaColaborador.getDataSaida() != null) {
                                    existingPessoaColaborador.setDataSaida(pessoaColaborador.getDataSaida());
                                }
                                if (pessoaColaborador.getCargaHoraria() != null) {
                                    existingPessoaColaborador.setCargaHoraria(pessoaColaborador.getCargaHoraria());
                                }
                                if (pessoaColaborador.getPrimeiroHorario() != null) {
                                    existingPessoaColaborador.setPrimeiroHorario(pessoaColaborador.getPrimeiroHorario());
                                }
                                if (pessoaColaborador.getSegundoHorario() != null) {
                                    existingPessoaColaborador.setSegundoHorario(pessoaColaborador.getSegundoHorario());
                                }
                                if (pessoaColaborador.getSalario() != null) {
                                    existingPessoaColaborador.setSalario(pessoaColaborador.getSalario());
                                }
                                if (pessoaColaborador.getComissao() != null) {
                                    existingPessoaColaborador.setComissao(pessoaColaborador.getComissao());
                                }
                                if (pessoaColaborador.getDescontoMaximo() != null) {
                                    existingPessoaColaborador.setDescontoMaximo(pessoaColaborador.getDescontoMaximo());
                                }

                                return existingPessoaColaborador;
                            }
                        )
                        .flatMap(pessoaColaboradorRepository::save);

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
     * {@code GET  /pessoa-colaboradors} : get all the pessoaColaboradors.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pessoaColaboradors in body.
     */
    @GetMapping("/pessoa-colaboradors")
    public Mono<List<PessoaColaborador>> getAllPessoaColaboradors() {
        log.debug("REST request to get all PessoaColaboradors");
        return pessoaColaboradorRepository.findAll().collectList();
    }

    /**
     * {@code GET  /pessoa-colaboradors} : get all the pessoaColaboradors as a stream.
     * @return the {@link Flux} of pessoaColaboradors.
     */
    @GetMapping(value = "/pessoa-colaboradors", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PessoaColaborador> getAllPessoaColaboradorsAsStream() {
        log.debug("REST request to get all PessoaColaboradors as a stream");
        return pessoaColaboradorRepository.findAll();
    }

    /**
     * {@code GET  /pessoa-colaboradors/:id} : get the "id" pessoaColaborador.
     *
     * @param id the id of the pessoaColaborador to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pessoaColaborador, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pessoa-colaboradors/{id}")
    public Mono<ResponseEntity<PessoaColaborador>> getPessoaColaborador(@PathVariable Long id) {
        log.debug("REST request to get PessoaColaborador : {}", id);
        Mono<PessoaColaborador> pessoaColaborador = pessoaColaboradorRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pessoaColaborador);
    }

    /**
     * {@code DELETE  /pessoa-colaboradors/:id} : delete the "id" pessoaColaborador.
     *
     * @param id the id of the pessoaColaborador to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pessoa-colaboradors/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePessoaColaborador(@PathVariable Long id) {
        log.debug("REST request to delete PessoaColaborador : {}", id);
        return pessoaColaboradorRepository
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
