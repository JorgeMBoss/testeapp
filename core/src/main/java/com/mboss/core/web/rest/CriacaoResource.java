package com.mboss.core.web.rest;

import com.mboss.core.domain.Criacao;
import com.mboss.core.repository.CriacaoRepository;
import com.mboss.core.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mboss.core.domain.Criacao}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CriacaoResource {

    private final Logger log = LoggerFactory.getLogger(CriacaoResource.class);

    private static final String ENTITY_NAME = "coreCriacao";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CriacaoRepository criacaoRepository;

    public CriacaoResource(CriacaoRepository criacaoRepository) {
        this.criacaoRepository = criacaoRepository;
    }

    /**
     * {@code POST  /criacaos} : Create a new criacao.
     *
     * @param criacao the criacao to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new criacao, or with status {@code 400 (Bad Request)} if the criacao has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/criacaos")
    public Mono<ResponseEntity<Criacao>> createCriacao(@Valid @RequestBody Criacao criacao) throws URISyntaxException {
        log.debug("REST request to save Criacao : {}", criacao);
        if (criacao.getId() != null) {
            throw new BadRequestAlertException("A new criacao cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return criacaoRepository
            .save(criacao)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/criacaos/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /criacaos/:id} : Updates an existing criacao.
     *
     * @param id the id of the criacao to save.
     * @param criacao the criacao to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated criacao,
     * or with status {@code 400 (Bad Request)} if the criacao is not valid,
     * or with status {@code 500 (Internal Server Error)} if the criacao couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/criacaos/{id}")
    public Mono<ResponseEntity<Criacao>> updateCriacao(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Criacao criacao
    ) throws URISyntaxException {
        log.debug("REST request to update Criacao : {}, {}", id, criacao);
        if (criacao.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, criacao.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return criacaoRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return criacaoRepository
                        .save(criacao)
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
     * {@code PATCH  /criacaos/:id} : Partial updates given fields of an existing criacao, field will ignore if it is null
     *
     * @param id the id of the criacao to save.
     * @param criacao the criacao to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated criacao,
     * or with status {@code 400 (Bad Request)} if the criacao is not valid,
     * or with status {@code 404 (Not Found)} if the criacao is not found,
     * or with status {@code 500 (Internal Server Error)} if the criacao couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/criacaos/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<Criacao>> partialUpdateCriacao(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Criacao criacao
    ) throws URISyntaxException {
        log.debug("REST request to partial update Criacao partially : {}, {}", id, criacao);
        if (criacao.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, criacao.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return criacaoRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Criacao> result = criacaoRepository
                        .findById(criacao.getId())
                        .map(
                            existingCriacao -> {
                                if (criacao.getIdEmpresa() != null) {
                                    existingCriacao.setIdEmpresa(criacao.getIdEmpresa());
                                }
                                if (criacao.getNome() != null) {
                                    existingCriacao.setNome(criacao.getNome());
                                }
                                if (criacao.getSexo() != null) {
                                    existingCriacao.setSexo(criacao.getSexo());
                                }
                                if (criacao.getPorte() != null) {
                                    existingCriacao.setPorte(criacao.getPorte());
                                }
                                if (criacao.getIdade() != null) {
                                    existingCriacao.setIdade(criacao.getIdade());
                                }
                                if (criacao.getDataNascimento() != null) {
                                    existingCriacao.setDataNascimento(criacao.getDataNascimento());
                                }
                                if (criacao.getCastrado() != null) {
                                    existingCriacao.setCastrado(criacao.getCastrado());
                                }
                                if (criacao.getAnotacao() != null) {
                                    existingCriacao.setAnotacao(criacao.getAnotacao());
                                }
                                if (criacao.getPedigree() != null) {
                                    existingCriacao.setPedigree(criacao.getPedigree());
                                }
                                if (criacao.getAtivo() != null) {
                                    existingCriacao.setAtivo(criacao.getAtivo());
                                }

                                return existingCriacao;
                            }
                        )
                        .flatMap(criacaoRepository::save);

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
     * {@code GET  /criacaos} : get all the criacaos.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of criacaos in body.
     */
    @GetMapping("/criacaos")
    public Mono<ResponseEntity<List<Criacao>>> getAllCriacaos(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Criacaos");
        return criacaoRepository
            .count()
            .zipWith(criacaoRepository.findAllBy(pageable).collectList())
            .map(
                countWithEntities -> {
                    return ResponseEntity
                        .ok()
                        .headers(
                            PaginationUtil.generatePaginationHttpHeaders(
                                UriComponentsBuilder.fromHttpRequest(request),
                                new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                            )
                        )
                        .body(countWithEntities.getT2());
                }
            );
    }

    /**
     * {@code GET  /criacaos/:id} : get the "id" criacao.
     *
     * @param id the id of the criacao to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the criacao, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/criacaos/{id}")
    public Mono<ResponseEntity<Criacao>> getCriacao(@PathVariable Long id) {
        log.debug("REST request to get Criacao : {}", id);
        Mono<Criacao> criacao = criacaoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(criacao);
    }

    /**
     * {@code DELETE  /criacaos/:id} : delete the "id" criacao.
     *
     * @param id the id of the criacao to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/criacaos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCriacao(@PathVariable Long id) {
        log.debug("REST request to delete Criacao : {}", id);
        return criacaoRepository
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
