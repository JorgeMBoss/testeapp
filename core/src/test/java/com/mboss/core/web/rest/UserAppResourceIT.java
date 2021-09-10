package com.mboss.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mboss.core.IntegrationTest;
import com.mboss.core.domain.UserApp;
import com.mboss.core.repository.UserAppRepository;
import com.mboss.core.repository.UserRepository;
import com.mboss.core.service.EntityManager;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link UserAppResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class UserAppResourceIT {

    private static final String ENTITY_API_URL = "/api/user-apps";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserAppRepository userAppRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private UserApp userApp;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserApp createEntity(EntityManager em) {
        UserApp userApp = new UserApp();
        return userApp;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserApp createUpdatedEntity(EntityManager em) {
        UserApp userApp = new UserApp();
        return userApp;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(UserApp.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        userApp = createEntity(em);
    }

    @Test
    void createUserApp() throws Exception {
        int databaseSizeBeforeCreate = userAppRepository.findAll().collectList().block().size();
        // Create the UserApp
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userApp))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the UserApp in the database
        List<UserApp> userAppList = userAppRepository.findAll().collectList().block();
        assertThat(userAppList).hasSize(databaseSizeBeforeCreate + 1);
        UserApp testUserApp = userAppList.get(userAppList.size() - 1);
    }

    @Test
    void createUserAppWithExistingId() throws Exception {
        // Create the UserApp with an existing ID
        userApp.setId(1L);

        int databaseSizeBeforeCreate = userAppRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userApp))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserApp in the database
        List<UserApp> userAppList = userAppRepository.findAll().collectList().block();
        assertThat(userAppList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllUserAppsAsStream() {
        // Initialize the database
        userAppRepository.save(userApp).block();

        List<UserApp> userAppList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(UserApp.class)
            .getResponseBody()
            .filter(userApp::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(userAppList).isNotNull();
        assertThat(userAppList).hasSize(1);
        UserApp testUserApp = userAppList.get(0);
    }

    @Test
    void getAllUserApps() {
        // Initialize the database
        userAppRepository.save(userApp).block();

        // Get all the userAppList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(userApp.getId().intValue()));
    }

    @Test
    void getUserApp() {
        // Initialize the database
        userAppRepository.save(userApp).block();

        // Get the userApp
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, userApp.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(userApp.getId().intValue()));
    }

    @Test
    void getNonExistingUserApp() {
        // Get the userApp
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewUserApp() throws Exception {
        // Initialize the database
        userAppRepository.save(userApp).block();

        int databaseSizeBeforeUpdate = userAppRepository.findAll().collectList().block().size();

        // Update the userApp
        UserApp updatedUserApp = userAppRepository.findById(userApp.getId()).block();

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedUserApp.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedUserApp))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserApp in the database
        List<UserApp> userAppList = userAppRepository.findAll().collectList().block();
        assertThat(userAppList).hasSize(databaseSizeBeforeUpdate);
        UserApp testUserApp = userAppList.get(userAppList.size() - 1);
    }

    @Test
    void putNonExistingUserApp() throws Exception {
        int databaseSizeBeforeUpdate = userAppRepository.findAll().collectList().block().size();
        userApp.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userApp.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userApp))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserApp in the database
        List<UserApp> userAppList = userAppRepository.findAll().collectList().block();
        assertThat(userAppList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchUserApp() throws Exception {
        int databaseSizeBeforeUpdate = userAppRepository.findAll().collectList().block().size();
        userApp.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userApp))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserApp in the database
        List<UserApp> userAppList = userAppRepository.findAll().collectList().block();
        assertThat(userAppList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamUserApp() throws Exception {
        int databaseSizeBeforeUpdate = userAppRepository.findAll().collectList().block().size();
        userApp.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userApp))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserApp in the database
        List<UserApp> userAppList = userAppRepository.findAll().collectList().block();
        assertThat(userAppList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateUserAppWithPatch() throws Exception {
        // Initialize the database
        userAppRepository.save(userApp).block();

        int databaseSizeBeforeUpdate = userAppRepository.findAll().collectList().block().size();

        // Update the userApp using partial update
        UserApp partialUpdatedUserApp = new UserApp();
        partialUpdatedUserApp.setId(userApp.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserApp.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUserApp))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserApp in the database
        List<UserApp> userAppList = userAppRepository.findAll().collectList().block();
        assertThat(userAppList).hasSize(databaseSizeBeforeUpdate);
        UserApp testUserApp = userAppList.get(userAppList.size() - 1);
    }

    @Test
    void fullUpdateUserAppWithPatch() throws Exception {
        // Initialize the database
        userAppRepository.save(userApp).block();

        int databaseSizeBeforeUpdate = userAppRepository.findAll().collectList().block().size();

        // Update the userApp using partial update
        UserApp partialUpdatedUserApp = new UserApp();
        partialUpdatedUserApp.setId(userApp.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserApp.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUserApp))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserApp in the database
        List<UserApp> userAppList = userAppRepository.findAll().collectList().block();
        assertThat(userAppList).hasSize(databaseSizeBeforeUpdate);
        UserApp testUserApp = userAppList.get(userAppList.size() - 1);
    }

    @Test
    void patchNonExistingUserApp() throws Exception {
        int databaseSizeBeforeUpdate = userAppRepository.findAll().collectList().block().size();
        userApp.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, userApp.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userApp))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserApp in the database
        List<UserApp> userAppList = userAppRepository.findAll().collectList().block();
        assertThat(userAppList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchUserApp() throws Exception {
        int databaseSizeBeforeUpdate = userAppRepository.findAll().collectList().block().size();
        userApp.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userApp))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserApp in the database
        List<UserApp> userAppList = userAppRepository.findAll().collectList().block();
        assertThat(userAppList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamUserApp() throws Exception {
        int databaseSizeBeforeUpdate = userAppRepository.findAll().collectList().block().size();
        userApp.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userApp))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserApp in the database
        List<UserApp> userAppList = userAppRepository.findAll().collectList().block();
        assertThat(userAppList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteUserApp() {
        // Initialize the database
        userAppRepository.save(userApp).block();

        int databaseSizeBeforeDelete = userAppRepository.findAll().collectList().block().size();

        // Delete the userApp
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, userApp.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<UserApp> userAppList = userAppRepository.findAll().collectList().block();
        assertThat(userAppList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
