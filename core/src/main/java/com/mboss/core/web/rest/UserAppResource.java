package com.mboss.core.web.rest;

import com.mboss.core.domain.UserApp;
import com.mboss.core.repository.UserAppRepository;
import com.mboss.core.repository.UserRepository;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mboss.core.domain.UserApp}.
 */
@RestController
@RequestMapping("/api")
public class UserAppResource {

    private final Logger log = LoggerFactory.getLogger(UserAppResource.class);

    private static final String ENTITY_NAME = "coreUserApp";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserAppRepository userAppRepository;

    private final UserRepository userRepository;

    public UserAppResource(UserAppRepository userAppRepository, UserRepository userRepository) {
        this.userAppRepository = userAppRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST  /user-apps} : Create a new userApp.
     *
     * @param userApp the userApp to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userApp, or with status {@code 400 (Bad Request)} if the userApp has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-apps")
    public Mono<ResponseEntity<UserApp>> createUserApp(@RequestBody UserApp userApp) throws URISyntaxException {
        log.debug("REST request to save UserApp : {}", userApp);
        if (userApp.getId() != null) {
            throw new BadRequestAlertException("A new userApp cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (userApp.getUser() != null) {
            // Save user in case it's new and only exists in gateway
            userRepository.save(userApp.getUser());
        }
        return userAppRepository
            .save(userApp)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/user-apps/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /user-apps/:id} : Updates an existing userApp.
     *
     * @param id the id of the userApp to save.
     * @param userApp the userApp to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userApp,
     * or with status {@code 400 (Bad Request)} if the userApp is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userApp couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-apps/{id}")
    public Mono<ResponseEntity<UserApp>> updateUserApp(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserApp userApp
    ) throws URISyntaxException {
        log.debug("REST request to update UserApp : {}, {}", id, userApp);
        if (userApp.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userApp.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userAppRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    if (userApp.getUser() != null) {
                        // Save user in case it's new and only exists in gateway
                        userRepository.save(userApp.getUser());
                    }
                    return userAppRepository
                        .save(userApp)
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
     * {@code PATCH  /user-apps/:id} : Partial updates given fields of an existing userApp, field will ignore if it is null
     *
     * @param id the id of the userApp to save.
     * @param userApp the userApp to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userApp,
     * or with status {@code 400 (Bad Request)} if the userApp is not valid,
     * or with status {@code 404 (Not Found)} if the userApp is not found,
     * or with status {@code 500 (Internal Server Error)} if the userApp couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-apps/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<UserApp>> partialUpdateUserApp(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserApp userApp
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserApp partially : {}, {}", id, userApp);
        if (userApp.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userApp.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userAppRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    if (userApp.getUser() != null) {
                        // Save user in case it's new and only exists in gateway
                        userRepository.save(userApp.getUser());
                    }

                    Mono<UserApp> result = userAppRepository
                        .findById(userApp.getId())
                        .map(
                            existingUserApp -> {
                                return existingUserApp;
                            }
                        )
                        .flatMap(userAppRepository::save);

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
     * {@code GET  /user-apps} : get all the userApps.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userApps in body.
     */
    @GetMapping("/user-apps")
    public Mono<List<UserApp>> getAllUserApps() {
        log.debug("REST request to get all UserApps");
        return userAppRepository.findAll().collectList();
    }

    /**
     * {@code GET  /user-apps} : get all the userApps as a stream.
     * @return the {@link Flux} of userApps.
     */
    @GetMapping(value = "/user-apps", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<UserApp> getAllUserAppsAsStream() {
        log.debug("REST request to get all UserApps as a stream");
        return userAppRepository.findAll();
    }

    /**
     * {@code GET  /user-apps/:id} : get the "id" userApp.
     *
     * @param id the id of the userApp to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userApp, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-apps/{id}")
    public Mono<ResponseEntity<UserApp>> getUserApp(@PathVariable Long id) {
        log.debug("REST request to get UserApp : {}", id);
        Mono<UserApp> userApp = userAppRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(userApp);
    }

    /**
     * {@code DELETE  /user-apps/:id} : delete the "id" userApp.
     *
     * @param id the id of the userApp to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-apps/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteUserApp(@PathVariable Long id) {
        log.debug("REST request to delete UserApp : {}", id);
        return userAppRepository
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
