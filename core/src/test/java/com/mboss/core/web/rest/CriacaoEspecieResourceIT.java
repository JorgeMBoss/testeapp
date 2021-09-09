package com.mboss.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mboss.core.IntegrationTest;
import com.mboss.core.domain.CriacaoEspecie;
import com.mboss.core.repository.CriacaoEspecieRepository;
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
 * Integration tests for the {@link CriacaoEspecieResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CriacaoEspecieResourceIT {

    private static final String DEFAULT_DESCRICAO = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ATIVO = false;
    private static final Boolean UPDATED_ATIVO = true;

    private static final String ENTITY_API_URL = "/api/criacao-especies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CriacaoEspecieRepository criacaoEspecieRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private CriacaoEspecie criacaoEspecie;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CriacaoEspecie createEntity(EntityManager em) {
        CriacaoEspecie criacaoEspecie = new CriacaoEspecie().descricao(DEFAULT_DESCRICAO).ativo(DEFAULT_ATIVO);
        return criacaoEspecie;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CriacaoEspecie createUpdatedEntity(EntityManager em) {
        CriacaoEspecie criacaoEspecie = new CriacaoEspecie().descricao(UPDATED_DESCRICAO).ativo(UPDATED_ATIVO);
        return criacaoEspecie;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(CriacaoEspecie.class).block();
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
        criacaoEspecie = createEntity(em);
    }

    @Test
    void createCriacaoEspecie() throws Exception {
        int databaseSizeBeforeCreate = criacaoEspecieRepository.findAll().collectList().block().size();
        // Create the CriacaoEspecie
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoEspecie))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the CriacaoEspecie in the database
        List<CriacaoEspecie> criacaoEspecieList = criacaoEspecieRepository.findAll().collectList().block();
        assertThat(criacaoEspecieList).hasSize(databaseSizeBeforeCreate + 1);
        CriacaoEspecie testCriacaoEspecie = criacaoEspecieList.get(criacaoEspecieList.size() - 1);
        assertThat(testCriacaoEspecie.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testCriacaoEspecie.getAtivo()).isEqualTo(DEFAULT_ATIVO);
    }

    @Test
    void createCriacaoEspecieWithExistingId() throws Exception {
        // Create the CriacaoEspecie with an existing ID
        criacaoEspecie.setId(1L);

        int databaseSizeBeforeCreate = criacaoEspecieRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoEspecie))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoEspecie in the database
        List<CriacaoEspecie> criacaoEspecieList = criacaoEspecieRepository.findAll().collectList().block();
        assertThat(criacaoEspecieList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCriacaoEspeciesAsStream() {
        // Initialize the database
        criacaoEspecieRepository.save(criacaoEspecie).block();

        List<CriacaoEspecie> criacaoEspecieList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(CriacaoEspecie.class)
            .getResponseBody()
            .filter(criacaoEspecie::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(criacaoEspecieList).isNotNull();
        assertThat(criacaoEspecieList).hasSize(1);
        CriacaoEspecie testCriacaoEspecie = criacaoEspecieList.get(0);
        assertThat(testCriacaoEspecie.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testCriacaoEspecie.getAtivo()).isEqualTo(DEFAULT_ATIVO);
    }

    @Test
    void getAllCriacaoEspecies() {
        // Initialize the database
        criacaoEspecieRepository.save(criacaoEspecie).block();

        // Get all the criacaoEspecieList
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
            .value(hasItem(criacaoEspecie.getId().intValue()))
            .jsonPath("$.[*].descricao")
            .value(hasItem(DEFAULT_DESCRICAO))
            .jsonPath("$.[*].ativo")
            .value(hasItem(DEFAULT_ATIVO.booleanValue()));
    }

    @Test
    void getCriacaoEspecie() {
        // Initialize the database
        criacaoEspecieRepository.save(criacaoEspecie).block();

        // Get the criacaoEspecie
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, criacaoEspecie.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(criacaoEspecie.getId().intValue()))
            .jsonPath("$.descricao")
            .value(is(DEFAULT_DESCRICAO))
            .jsonPath("$.ativo")
            .value(is(DEFAULT_ATIVO.booleanValue()));
    }

    @Test
    void getNonExistingCriacaoEspecie() {
        // Get the criacaoEspecie
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCriacaoEspecie() throws Exception {
        // Initialize the database
        criacaoEspecieRepository.save(criacaoEspecie).block();

        int databaseSizeBeforeUpdate = criacaoEspecieRepository.findAll().collectList().block().size();

        // Update the criacaoEspecie
        CriacaoEspecie updatedCriacaoEspecie = criacaoEspecieRepository.findById(criacaoEspecie.getId()).block();
        updatedCriacaoEspecie.descricao(UPDATED_DESCRICAO).ativo(UPDATED_ATIVO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCriacaoEspecie.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCriacaoEspecie))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CriacaoEspecie in the database
        List<CriacaoEspecie> criacaoEspecieList = criacaoEspecieRepository.findAll().collectList().block();
        assertThat(criacaoEspecieList).hasSize(databaseSizeBeforeUpdate);
        CriacaoEspecie testCriacaoEspecie = criacaoEspecieList.get(criacaoEspecieList.size() - 1);
        assertThat(testCriacaoEspecie.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testCriacaoEspecie.getAtivo()).isEqualTo(UPDATED_ATIVO);
    }

    @Test
    void putNonExistingCriacaoEspecie() throws Exception {
        int databaseSizeBeforeUpdate = criacaoEspecieRepository.findAll().collectList().block().size();
        criacaoEspecie.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, criacaoEspecie.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoEspecie))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoEspecie in the database
        List<CriacaoEspecie> criacaoEspecieList = criacaoEspecieRepository.findAll().collectList().block();
        assertThat(criacaoEspecieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCriacaoEspecie() throws Exception {
        int databaseSizeBeforeUpdate = criacaoEspecieRepository.findAll().collectList().block().size();
        criacaoEspecie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoEspecie))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoEspecie in the database
        List<CriacaoEspecie> criacaoEspecieList = criacaoEspecieRepository.findAll().collectList().block();
        assertThat(criacaoEspecieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCriacaoEspecie() throws Exception {
        int databaseSizeBeforeUpdate = criacaoEspecieRepository.findAll().collectList().block().size();
        criacaoEspecie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoEspecie))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CriacaoEspecie in the database
        List<CriacaoEspecie> criacaoEspecieList = criacaoEspecieRepository.findAll().collectList().block();
        assertThat(criacaoEspecieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCriacaoEspecieWithPatch() throws Exception {
        // Initialize the database
        criacaoEspecieRepository.save(criacaoEspecie).block();

        int databaseSizeBeforeUpdate = criacaoEspecieRepository.findAll().collectList().block().size();

        // Update the criacaoEspecie using partial update
        CriacaoEspecie partialUpdatedCriacaoEspecie = new CriacaoEspecie();
        partialUpdatedCriacaoEspecie.setId(criacaoEspecie.getId());

        partialUpdatedCriacaoEspecie.descricao(UPDATED_DESCRICAO).ativo(UPDATED_ATIVO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCriacaoEspecie.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCriacaoEspecie))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CriacaoEspecie in the database
        List<CriacaoEspecie> criacaoEspecieList = criacaoEspecieRepository.findAll().collectList().block();
        assertThat(criacaoEspecieList).hasSize(databaseSizeBeforeUpdate);
        CriacaoEspecie testCriacaoEspecie = criacaoEspecieList.get(criacaoEspecieList.size() - 1);
        assertThat(testCriacaoEspecie.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testCriacaoEspecie.getAtivo()).isEqualTo(UPDATED_ATIVO);
    }

    @Test
    void fullUpdateCriacaoEspecieWithPatch() throws Exception {
        // Initialize the database
        criacaoEspecieRepository.save(criacaoEspecie).block();

        int databaseSizeBeforeUpdate = criacaoEspecieRepository.findAll().collectList().block().size();

        // Update the criacaoEspecie using partial update
        CriacaoEspecie partialUpdatedCriacaoEspecie = new CriacaoEspecie();
        partialUpdatedCriacaoEspecie.setId(criacaoEspecie.getId());

        partialUpdatedCriacaoEspecie.descricao(UPDATED_DESCRICAO).ativo(UPDATED_ATIVO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCriacaoEspecie.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCriacaoEspecie))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CriacaoEspecie in the database
        List<CriacaoEspecie> criacaoEspecieList = criacaoEspecieRepository.findAll().collectList().block();
        assertThat(criacaoEspecieList).hasSize(databaseSizeBeforeUpdate);
        CriacaoEspecie testCriacaoEspecie = criacaoEspecieList.get(criacaoEspecieList.size() - 1);
        assertThat(testCriacaoEspecie.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testCriacaoEspecie.getAtivo()).isEqualTo(UPDATED_ATIVO);
    }

    @Test
    void patchNonExistingCriacaoEspecie() throws Exception {
        int databaseSizeBeforeUpdate = criacaoEspecieRepository.findAll().collectList().block().size();
        criacaoEspecie.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, criacaoEspecie.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoEspecie))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoEspecie in the database
        List<CriacaoEspecie> criacaoEspecieList = criacaoEspecieRepository.findAll().collectList().block();
        assertThat(criacaoEspecieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCriacaoEspecie() throws Exception {
        int databaseSizeBeforeUpdate = criacaoEspecieRepository.findAll().collectList().block().size();
        criacaoEspecie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoEspecie))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoEspecie in the database
        List<CriacaoEspecie> criacaoEspecieList = criacaoEspecieRepository.findAll().collectList().block();
        assertThat(criacaoEspecieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCriacaoEspecie() throws Exception {
        int databaseSizeBeforeUpdate = criacaoEspecieRepository.findAll().collectList().block().size();
        criacaoEspecie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoEspecie))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CriacaoEspecie in the database
        List<CriacaoEspecie> criacaoEspecieList = criacaoEspecieRepository.findAll().collectList().block();
        assertThat(criacaoEspecieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCriacaoEspecie() {
        // Initialize the database
        criacaoEspecieRepository.save(criacaoEspecie).block();

        int databaseSizeBeforeDelete = criacaoEspecieRepository.findAll().collectList().block().size();

        // Delete the criacaoEspecie
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, criacaoEspecie.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<CriacaoEspecie> criacaoEspecieList = criacaoEspecieRepository.findAll().collectList().block();
        assertThat(criacaoEspecieList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
