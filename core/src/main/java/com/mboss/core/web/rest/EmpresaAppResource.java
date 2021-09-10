package com.mboss.core.web.rest;

import com.mboss.core.domain.EmpresaApp;
import com.mboss.core.repository.EmpresaAppRepository;
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
 * REST controller for managing {@link com.mboss.core.domain.EmpresaApp}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class EmpresaAppResource {

    private final Logger log = LoggerFactory.getLogger(EmpresaAppResource.class);

    private static final String ENTITY_NAME = "coreEmpresaApp";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EmpresaAppRepository empresaAppRepository;

    public EmpresaAppResource(EmpresaAppRepository empresaAppRepository) {
        this.empresaAppRepository = empresaAppRepository;
    }

    /**
     * {@code POST  /empresa-apps} : Create a new empresaApp.
     *
     * @param empresaApp the empresaApp to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new empresaApp, or with status {@code 400 (Bad Request)} if the empresaApp has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/empresa-apps")
    public Mono<ResponseEntity<EmpresaApp>> createEmpresaApp(@Valid @RequestBody EmpresaApp empresaApp) throws URISyntaxException {
        log.debug("REST request to save EmpresaApp : {}", empresaApp);
        if (empresaApp.getId() != null) {
            throw new BadRequestAlertException("A new empresaApp cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return empresaAppRepository
            .save(empresaApp)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/empresa-apps/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /empresa-apps/:id} : Updates an existing empresaApp.
     *
     * @param id the id of the empresaApp to save.
     * @param empresaApp the empresaApp to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated empresaApp,
     * or with status {@code 400 (Bad Request)} if the empresaApp is not valid,
     * or with status {@code 500 (Internal Server Error)} if the empresaApp couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/empresa-apps/{id}")
    public Mono<ResponseEntity<EmpresaApp>> updateEmpresaApp(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EmpresaApp empresaApp
    ) throws URISyntaxException {
        log.debug("REST request to update EmpresaApp : {}, {}", id, empresaApp);
        if (empresaApp.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, empresaApp.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return empresaAppRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return empresaAppRepository
                        .save(empresaApp)
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
     * {@code PATCH  /empresa-apps/:id} : Partial updates given fields of an existing empresaApp, field will ignore if it is null
     *
     * @param id the id of the empresaApp to save.
     * @param empresaApp the empresaApp to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated empresaApp,
     * or with status {@code 400 (Bad Request)} if the empresaApp is not valid,
     * or with status {@code 404 (Not Found)} if the empresaApp is not found,
     * or with status {@code 500 (Internal Server Error)} if the empresaApp couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/empresa-apps/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<EmpresaApp>> partialUpdateEmpresaApp(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EmpresaApp empresaApp
    ) throws URISyntaxException {
        log.debug("REST request to partial update EmpresaApp partially : {}, {}", id, empresaApp);
        if (empresaApp.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, empresaApp.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return empresaAppRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<EmpresaApp> result = empresaAppRepository
                        .findById(empresaApp.getId())
                        .map(
                            existingEmpresaApp -> {
                                if (empresaApp.getRazaoSocial() != null) {
                                    existingEmpresaApp.setRazaoSocial(empresaApp.getRazaoSocial());
                                }
                                if (empresaApp.getNomeFantasia() != null) {
                                    existingEmpresaApp.setNomeFantasia(empresaApp.getNomeFantasia());
                                }

                                return existingEmpresaApp;
                            }
                        )
                        .flatMap(empresaAppRepository::save);

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
     * {@code GET  /empresa-apps} : get all the empresaApps.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of empresaApps in body.
     */
    @GetMapping("/empresa-apps")
    public Mono<ResponseEntity<List<EmpresaApp>>> getAllEmpresaApps(
        Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of EmpresaApps");
        return empresaAppRepository
            .count()
            .zipWith(empresaAppRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /empresa-apps/:id} : get the "id" empresaApp.
     *
     * @param id the id of the empresaApp to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the empresaApp, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/empresa-apps/{id}")
    public Mono<ResponseEntity<EmpresaApp>> getEmpresaApp(@PathVariable Long id) {
        log.debug("REST request to get EmpresaApp : {}", id);
        Mono<EmpresaApp> empresaApp = empresaAppRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(empresaApp);
    }

    /**
     * {@code DELETE  /empresa-apps/:id} : delete the "id" empresaApp.
     *
     * @param id the id of the empresaApp to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/empresa-apps/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteEmpresaApp(@PathVariable Long id) {
        log.debug("REST request to delete EmpresaApp : {}", id);
        return empresaAppRepository
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
