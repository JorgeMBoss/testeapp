package com.mboss.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mboss.core.IntegrationTest;
import com.mboss.core.domain.CriacaoCor;
import com.mboss.core.repository.CriacaoCorRepository;
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
 * Integration tests for the {@link CriacaoCorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CriacaoCorResourceIT {

    private static final String DEFAULT_DESCRICAO = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ATIVO = false;
    private static final Boolean UPDATED_ATIVO = true;

    private static final String ENTITY_API_URL = "/api/criacao-cors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CriacaoCorRepository criacaoCorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private CriacaoCor criacaoCor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CriacaoCor createEntity(EntityManager em) {
        CriacaoCor criacaoCor = new CriacaoCor().descricao(DEFAULT_DESCRICAO).ativo(DEFAULT_ATIVO);
        return criacaoCor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CriacaoCor createUpdatedEntity(EntityManager em) {
        CriacaoCor criacaoCor = new CriacaoCor().descricao(UPDATED_DESCRICAO).ativo(UPDATED_ATIVO);
        return criacaoCor;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(CriacaoCor.class).block();
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
        criacaoCor = createEntity(em);
    }

    @Test
    void createCriacaoCor() throws Exception {
        int databaseSizeBeforeCreate = criacaoCorRepository.findAll().collectList().block().size();
        // Create the CriacaoCor
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoCor))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the CriacaoCor in the database
        List<CriacaoCor> criacaoCorList = criacaoCorRepository.findAll().collectList().block();
        assertThat(criacaoCorList).hasSize(databaseSizeBeforeCreate + 1);
        CriacaoCor testCriacaoCor = criacaoCorList.get(criacaoCorList.size() - 1);
        assertThat(testCriacaoCor.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testCriacaoCor.getAtivo()).isEqualTo(DEFAULT_ATIVO);
    }

    @Test
    void createCriacaoCorWithExistingId() throws Exception {
        // Create the CriacaoCor with an existing ID
        criacaoCor.setId(1L);

        int databaseSizeBeforeCreate = criacaoCorRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoCor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoCor in the database
        List<CriacaoCor> criacaoCorList = criacaoCorRepository.findAll().collectList().block();
        assertThat(criacaoCorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCriacaoCorsAsStream() {
        // Initialize the database
        criacaoCorRepository.save(criacaoCor).block();

        List<CriacaoCor> criacaoCorList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(CriacaoCor.class)
            .getResponseBody()
            .filter(criacaoCor::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(criacaoCorList).isNotNull();
        assertThat(criacaoCorList).hasSize(1);
        CriacaoCor testCriacaoCor = criacaoCorList.get(0);
        assertThat(testCriacaoCor.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testCriacaoCor.getAtivo()).isEqualTo(DEFAULT_ATIVO);
    }

    @Test
    void getAllCriacaoCors() {
        // Initialize the database
        criacaoCorRepository.save(criacaoCor).block();

        // Get all the criacaoCorList
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
            .value(hasItem(criacaoCor.getId().intValue()))
            .jsonPath("$.[*].descricao")
            .value(hasItem(DEFAULT_DESCRICAO))
            .jsonPath("$.[*].ativo")
            .value(hasItem(DEFAULT_ATIVO.booleanValue()));
    }

    @Test
    void getCriacaoCor() {
        // Initialize the database
        criacaoCorRepository.save(criacaoCor).block();

        // Get the criacaoCor
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, criacaoCor.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(criacaoCor.getId().intValue()))
            .jsonPath("$.descricao")
            .value(is(DEFAULT_DESCRICAO))
            .jsonPath("$.ativo")
            .value(is(DEFAULT_ATIVO.booleanValue()));
    }

    @Test
    void getNonExistingCriacaoCor() {
        // Get the criacaoCor
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCriacaoCor() throws Exception {
        // Initialize the database
        criacaoCorRepository.save(criacaoCor).block();

        int databaseSizeBeforeUpdate = criacaoCorRepository.findAll().collectList().block().size();

        // Update the criacaoCor
        CriacaoCor updatedCriacaoCor = criacaoCorRepository.findById(criacaoCor.getId()).block();
        updatedCriacaoCor.descricao(UPDATED_DESCRICAO).ativo(UPDATED_ATIVO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCriacaoCor.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCriacaoCor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CriacaoCor in the database
        List<CriacaoCor> criacaoCorList = criacaoCorRepository.findAll().collectList().block();
        assertThat(criacaoCorList).hasSize(databaseSizeBeforeUpdate);
        CriacaoCor testCriacaoCor = criacaoCorList.get(criacaoCorList.size() - 1);
        assertThat(testCriacaoCor.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testCriacaoCor.getAtivo()).isEqualTo(UPDATED_ATIVO);
    }

    @Test
    void putNonExistingCriacaoCor() throws Exception {
        int databaseSizeBeforeUpdate = criacaoCorRepository.findAll().collectList().block().size();
        criacaoCor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, criacaoCor.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoCor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoCor in the database
        List<CriacaoCor> criacaoCorList = criacaoCorRepository.findAll().collectList().block();
        assertThat(criacaoCorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCriacaoCor() throws Exception {
        int databaseSizeBeforeUpdate = criacaoCorRepository.findAll().collectList().block().size();
        criacaoCor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoCor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoCor in the database
        List<CriacaoCor> criacaoCorList = criacaoCorRepository.findAll().collectList().block();
        assertThat(criacaoCorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCriacaoCor() throws Exception {
        int databaseSizeBeforeUpdate = criacaoCorRepository.findAll().collectList().block().size();
        criacaoCor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoCor))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CriacaoCor in the database
        List<CriacaoCor> criacaoCorList = criacaoCorRepository.findAll().collectList().block();
        assertThat(criacaoCorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCriacaoCorWithPatch() throws Exception {
        // Initialize the database
        criacaoCorRepository.save(criacaoCor).block();

        int databaseSizeBeforeUpdate = criacaoCorRepository.findAll().collectList().block().size();

        // Update the criacaoCor using partial update
        CriacaoCor partialUpdatedCriacaoCor = new CriacaoCor();
        partialUpdatedCriacaoCor.setId(criacaoCor.getId());

        partialUpdatedCriacaoCor.descricao(UPDATED_DESCRICAO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCriacaoCor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCriacaoCor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CriacaoCor in the database
        List<CriacaoCor> criacaoCorList = criacaoCorRepository.findAll().collectList().block();
        assertThat(criacaoCorList).hasSize(databaseSizeBeforeUpdate);
        CriacaoCor testCriacaoCor = criacaoCorList.get(criacaoCorList.size() - 1);
        assertThat(testCriacaoCor.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testCriacaoCor.getAtivo()).isEqualTo(DEFAULT_ATIVO);
    }

    @Test
    void fullUpdateCriacaoCorWithPatch() throws Exception {
        // Initialize the database
        criacaoCorRepository.save(criacaoCor).block();

        int databaseSizeBeforeUpdate = criacaoCorRepository.findAll().collectList().block().size();

        // Update the criacaoCor using partial update
        CriacaoCor partialUpdatedCriacaoCor = new CriacaoCor();
        partialUpdatedCriacaoCor.setId(criacaoCor.getId());

        partialUpdatedCriacaoCor.descricao(UPDATED_DESCRICAO).ativo(UPDATED_ATIVO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCriacaoCor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCriacaoCor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CriacaoCor in the database
        List<CriacaoCor> criacaoCorList = criacaoCorRepository.findAll().collectList().block();
        assertThat(criacaoCorList).hasSize(databaseSizeBeforeUpdate);
        CriacaoCor testCriacaoCor = criacaoCorList.get(criacaoCorList.size() - 1);
        assertThat(testCriacaoCor.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testCriacaoCor.getAtivo()).isEqualTo(UPDATED_ATIVO);
    }

    @Test
    void patchNonExistingCriacaoCor() throws Exception {
        int databaseSizeBeforeUpdate = criacaoCorRepository.findAll().collectList().block().size();
        criacaoCor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, criacaoCor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoCor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoCor in the database
        List<CriacaoCor> criacaoCorList = criacaoCorRepository.findAll().collectList().block();
        assertThat(criacaoCorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCriacaoCor() throws Exception {
        int databaseSizeBeforeUpdate = criacaoCorRepository.findAll().collectList().block().size();
        criacaoCor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoCor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoCor in the database
        List<CriacaoCor> criacaoCorList = criacaoCorRepository.findAll().collectList().block();
        assertThat(criacaoCorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCriacaoCor() throws Exception {
        int databaseSizeBeforeUpdate = criacaoCorRepository.findAll().collectList().block().size();
        criacaoCor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoCor))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CriacaoCor in the database
        List<CriacaoCor> criacaoCorList = criacaoCorRepository.findAll().collectList().block();
        assertThat(criacaoCorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCriacaoCor() {
        // Initialize the database
        criacaoCorRepository.save(criacaoCor).block();

        int databaseSizeBeforeDelete = criacaoCorRepository.findAll().collectList().block().size();

        // Delete the criacaoCor
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, criacaoCor.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<CriacaoCor> criacaoCorList = criacaoCorRepository.findAll().collectList().block();
        assertThat(criacaoCorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
