package com.mboss.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mboss.core.IntegrationTest;
import com.mboss.core.domain.EmpresaApp;
import com.mboss.core.repository.EmpresaAppRepository;
import com.mboss.core.service.EntityManager;
import java.time.Duration;
import java.util.ArrayList;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link EmpresaAppResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class EmpresaAppResourceIT {

    private static final String DEFAULT_RAZAO_SOCIAL = "AAAAAAAAAA";
    private static final String UPDATED_RAZAO_SOCIAL = "BBBBBBBBBB";

    private static final String DEFAULT_NOME_FANTASIA = "AAAAAAAAAA";
    private static final String UPDATED_NOME_FANTASIA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/empresa-apps";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EmpresaAppRepository empresaAppRepository;

    @Mock
    private EmpresaAppRepository empresaAppRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private EmpresaApp empresaApp;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EmpresaApp createEntity(EntityManager em) {
        EmpresaApp empresaApp = new EmpresaApp().razaoSocial(DEFAULT_RAZAO_SOCIAL).nomeFantasia(DEFAULT_NOME_FANTASIA);
        return empresaApp;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EmpresaApp createUpdatedEntity(EntityManager em) {
        EmpresaApp empresaApp = new EmpresaApp().razaoSocial(UPDATED_RAZAO_SOCIAL).nomeFantasia(UPDATED_NOME_FANTASIA);
        return empresaApp;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_empresa_app__user_app").block();
            em.deleteAll(EmpresaApp.class).block();
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
        empresaApp = createEntity(em);
    }

    @Test
    void createEmpresaApp() throws Exception {
        int databaseSizeBeforeCreate = empresaAppRepository.findAll().collectList().block().size();
        // Create the EmpresaApp
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(empresaApp))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the EmpresaApp in the database
        List<EmpresaApp> empresaAppList = empresaAppRepository.findAll().collectList().block();
        assertThat(empresaAppList).hasSize(databaseSizeBeforeCreate + 1);
        EmpresaApp testEmpresaApp = empresaAppList.get(empresaAppList.size() - 1);
        assertThat(testEmpresaApp.getRazaoSocial()).isEqualTo(DEFAULT_RAZAO_SOCIAL);
        assertThat(testEmpresaApp.getNomeFantasia()).isEqualTo(DEFAULT_NOME_FANTASIA);
    }

    @Test
    void createEmpresaAppWithExistingId() throws Exception {
        // Create the EmpresaApp with an existing ID
        empresaApp.setId(1L);

        int databaseSizeBeforeCreate = empresaAppRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(empresaApp))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EmpresaApp in the database
        List<EmpresaApp> empresaAppList = empresaAppRepository.findAll().collectList().block();
        assertThat(empresaAppList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllEmpresaApps() {
        // Initialize the database
        empresaAppRepository.save(empresaApp).block();

        // Get all the empresaAppList
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
            .value(hasItem(empresaApp.getId().intValue()))
            .jsonPath("$.[*].razaoSocial")
            .value(hasItem(DEFAULT_RAZAO_SOCIAL))
            .jsonPath("$.[*].nomeFantasia")
            .value(hasItem(DEFAULT_NOME_FANTASIA));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEmpresaAppsWithEagerRelationshipsIsEnabled() {
        when(empresaAppRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(empresaAppRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEmpresaAppsWithEagerRelationshipsIsNotEnabled() {
        when(empresaAppRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(empresaAppRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getEmpresaApp() {
        // Initialize the database
        empresaAppRepository.save(empresaApp).block();

        // Get the empresaApp
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, empresaApp.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(empresaApp.getId().intValue()))
            .jsonPath("$.razaoSocial")
            .value(is(DEFAULT_RAZAO_SOCIAL))
            .jsonPath("$.nomeFantasia")
            .value(is(DEFAULT_NOME_FANTASIA));
    }

    @Test
    void getNonExistingEmpresaApp() {
        // Get the empresaApp
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewEmpresaApp() throws Exception {
        // Initialize the database
        empresaAppRepository.save(empresaApp).block();

        int databaseSizeBeforeUpdate = empresaAppRepository.findAll().collectList().block().size();

        // Update the empresaApp
        EmpresaApp updatedEmpresaApp = empresaAppRepository.findById(empresaApp.getId()).block();
        updatedEmpresaApp.razaoSocial(UPDATED_RAZAO_SOCIAL).nomeFantasia(UPDATED_NOME_FANTASIA);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedEmpresaApp.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedEmpresaApp))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EmpresaApp in the database
        List<EmpresaApp> empresaAppList = empresaAppRepository.findAll().collectList().block();
        assertThat(empresaAppList).hasSize(databaseSizeBeforeUpdate);
        EmpresaApp testEmpresaApp = empresaAppList.get(empresaAppList.size() - 1);
        assertThat(testEmpresaApp.getRazaoSocial()).isEqualTo(UPDATED_RAZAO_SOCIAL);
        assertThat(testEmpresaApp.getNomeFantasia()).isEqualTo(UPDATED_NOME_FANTASIA);
    }

    @Test
    void putNonExistingEmpresaApp() throws Exception {
        int databaseSizeBeforeUpdate = empresaAppRepository.findAll().collectList().block().size();
        empresaApp.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, empresaApp.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(empresaApp))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EmpresaApp in the database
        List<EmpresaApp> empresaAppList = empresaAppRepository.findAll().collectList().block();
        assertThat(empresaAppList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEmpresaApp() throws Exception {
        int databaseSizeBeforeUpdate = empresaAppRepository.findAll().collectList().block().size();
        empresaApp.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(empresaApp))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EmpresaApp in the database
        List<EmpresaApp> empresaAppList = empresaAppRepository.findAll().collectList().block();
        assertThat(empresaAppList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEmpresaApp() throws Exception {
        int databaseSizeBeforeUpdate = empresaAppRepository.findAll().collectList().block().size();
        empresaApp.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(empresaApp))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EmpresaApp in the database
        List<EmpresaApp> empresaAppList = empresaAppRepository.findAll().collectList().block();
        assertThat(empresaAppList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEmpresaAppWithPatch() throws Exception {
        // Initialize the database
        empresaAppRepository.save(empresaApp).block();

        int databaseSizeBeforeUpdate = empresaAppRepository.findAll().collectList().block().size();

        // Update the empresaApp using partial update
        EmpresaApp partialUpdatedEmpresaApp = new EmpresaApp();
        partialUpdatedEmpresaApp.setId(empresaApp.getId());

        partialUpdatedEmpresaApp.razaoSocial(UPDATED_RAZAO_SOCIAL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEmpresaApp.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEmpresaApp))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EmpresaApp in the database
        List<EmpresaApp> empresaAppList = empresaAppRepository.findAll().collectList().block();
        assertThat(empresaAppList).hasSize(databaseSizeBeforeUpdate);
        EmpresaApp testEmpresaApp = empresaAppList.get(empresaAppList.size() - 1);
        assertThat(testEmpresaApp.getRazaoSocial()).isEqualTo(UPDATED_RAZAO_SOCIAL);
        assertThat(testEmpresaApp.getNomeFantasia()).isEqualTo(DEFAULT_NOME_FANTASIA);
    }

    @Test
    void fullUpdateEmpresaAppWithPatch() throws Exception {
        // Initialize the database
        empresaAppRepository.save(empresaApp).block();

        int databaseSizeBeforeUpdate = empresaAppRepository.findAll().collectList().block().size();

        // Update the empresaApp using partial update
        EmpresaApp partialUpdatedEmpresaApp = new EmpresaApp();
        partialUpdatedEmpresaApp.setId(empresaApp.getId());

        partialUpdatedEmpresaApp.razaoSocial(UPDATED_RAZAO_SOCIAL).nomeFantasia(UPDATED_NOME_FANTASIA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEmpresaApp.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEmpresaApp))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EmpresaApp in the database
        List<EmpresaApp> empresaAppList = empresaAppRepository.findAll().collectList().block();
        assertThat(empresaAppList).hasSize(databaseSizeBeforeUpdate);
        EmpresaApp testEmpresaApp = empresaAppList.get(empresaAppList.size() - 1);
        assertThat(testEmpresaApp.getRazaoSocial()).isEqualTo(UPDATED_RAZAO_SOCIAL);
        assertThat(testEmpresaApp.getNomeFantasia()).isEqualTo(UPDATED_NOME_FANTASIA);
    }

    @Test
    void patchNonExistingEmpresaApp() throws Exception {
        int databaseSizeBeforeUpdate = empresaAppRepository.findAll().collectList().block().size();
        empresaApp.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, empresaApp.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(empresaApp))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EmpresaApp in the database
        List<EmpresaApp> empresaAppList = empresaAppRepository.findAll().collectList().block();
        assertThat(empresaAppList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEmpresaApp() throws Exception {
        int databaseSizeBeforeUpdate = empresaAppRepository.findAll().collectList().block().size();
        empresaApp.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(empresaApp))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EmpresaApp in the database
        List<EmpresaApp> empresaAppList = empresaAppRepository.findAll().collectList().block();
        assertThat(empresaAppList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEmpresaApp() throws Exception {
        int databaseSizeBeforeUpdate = empresaAppRepository.findAll().collectList().block().size();
        empresaApp.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(empresaApp))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EmpresaApp in the database
        List<EmpresaApp> empresaAppList = empresaAppRepository.findAll().collectList().block();
        assertThat(empresaAppList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEmpresaApp() {
        // Initialize the database
        empresaAppRepository.save(empresaApp).block();

        int databaseSizeBeforeDelete = empresaAppRepository.findAll().collectList().block().size();

        // Delete the empresaApp
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, empresaApp.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<EmpresaApp> empresaAppList = empresaAppRepository.findAll().collectList().block();
        assertThat(empresaAppList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
