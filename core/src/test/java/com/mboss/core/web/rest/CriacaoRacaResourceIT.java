package com.mboss.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mboss.core.IntegrationTest;
import com.mboss.core.domain.CriacaoRaca;
import com.mboss.core.repository.CriacaoRacaRepository;
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
 * Integration tests for the {@link CriacaoRacaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CriacaoRacaResourceIT {

    private static final String DEFAULT_DESCRICAO = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ATIVO = false;
    private static final Boolean UPDATED_ATIVO = true;

    private static final String ENTITY_API_URL = "/api/criacao-racas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CriacaoRacaRepository criacaoRacaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private CriacaoRaca criacaoRaca;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CriacaoRaca createEntity(EntityManager em) {
        CriacaoRaca criacaoRaca = new CriacaoRaca().descricao(DEFAULT_DESCRICAO).ativo(DEFAULT_ATIVO);
        return criacaoRaca;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CriacaoRaca createUpdatedEntity(EntityManager em) {
        CriacaoRaca criacaoRaca = new CriacaoRaca().descricao(UPDATED_DESCRICAO).ativo(UPDATED_ATIVO);
        return criacaoRaca;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(CriacaoRaca.class).block();
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
        criacaoRaca = createEntity(em);
    }

    @Test
    void createCriacaoRaca() throws Exception {
        int databaseSizeBeforeCreate = criacaoRacaRepository.findAll().collectList().block().size();
        // Create the CriacaoRaca
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoRaca))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the CriacaoRaca in the database
        List<CriacaoRaca> criacaoRacaList = criacaoRacaRepository.findAll().collectList().block();
        assertThat(criacaoRacaList).hasSize(databaseSizeBeforeCreate + 1);
        CriacaoRaca testCriacaoRaca = criacaoRacaList.get(criacaoRacaList.size() - 1);
        assertThat(testCriacaoRaca.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testCriacaoRaca.getAtivo()).isEqualTo(DEFAULT_ATIVO);
    }

    @Test
    void createCriacaoRacaWithExistingId() throws Exception {
        // Create the CriacaoRaca with an existing ID
        criacaoRaca.setId(1L);

        int databaseSizeBeforeCreate = criacaoRacaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoRaca))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoRaca in the database
        List<CriacaoRaca> criacaoRacaList = criacaoRacaRepository.findAll().collectList().block();
        assertThat(criacaoRacaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCriacaoRacasAsStream() {
        // Initialize the database
        criacaoRacaRepository.save(criacaoRaca).block();

        List<CriacaoRaca> criacaoRacaList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(CriacaoRaca.class)
            .getResponseBody()
            .filter(criacaoRaca::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(criacaoRacaList).isNotNull();
        assertThat(criacaoRacaList).hasSize(1);
        CriacaoRaca testCriacaoRaca = criacaoRacaList.get(0);
        assertThat(testCriacaoRaca.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testCriacaoRaca.getAtivo()).isEqualTo(DEFAULT_ATIVO);
    }

    @Test
    void getAllCriacaoRacas() {
        // Initialize the database
        criacaoRacaRepository.save(criacaoRaca).block();

        // Get all the criacaoRacaList
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
            .value(hasItem(criacaoRaca.getId().intValue()))
            .jsonPath("$.[*].descricao")
            .value(hasItem(DEFAULT_DESCRICAO))
            .jsonPath("$.[*].ativo")
            .value(hasItem(DEFAULT_ATIVO.booleanValue()));
    }

    @Test
    void getCriacaoRaca() {
        // Initialize the database
        criacaoRacaRepository.save(criacaoRaca).block();

        // Get the criacaoRaca
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, criacaoRaca.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(criacaoRaca.getId().intValue()))
            .jsonPath("$.descricao")
            .value(is(DEFAULT_DESCRICAO))
            .jsonPath("$.ativo")
            .value(is(DEFAULT_ATIVO.booleanValue()));
    }

    @Test
    void getNonExistingCriacaoRaca() {
        // Get the criacaoRaca
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCriacaoRaca() throws Exception {
        // Initialize the database
        criacaoRacaRepository.save(criacaoRaca).block();

        int databaseSizeBeforeUpdate = criacaoRacaRepository.findAll().collectList().block().size();

        // Update the criacaoRaca
        CriacaoRaca updatedCriacaoRaca = criacaoRacaRepository.findById(criacaoRaca.getId()).block();
        updatedCriacaoRaca.descricao(UPDATED_DESCRICAO).ativo(UPDATED_ATIVO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCriacaoRaca.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCriacaoRaca))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CriacaoRaca in the database
        List<CriacaoRaca> criacaoRacaList = criacaoRacaRepository.findAll().collectList().block();
        assertThat(criacaoRacaList).hasSize(databaseSizeBeforeUpdate);
        CriacaoRaca testCriacaoRaca = criacaoRacaList.get(criacaoRacaList.size() - 1);
        assertThat(testCriacaoRaca.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testCriacaoRaca.getAtivo()).isEqualTo(UPDATED_ATIVO);
    }

    @Test
    void putNonExistingCriacaoRaca() throws Exception {
        int databaseSizeBeforeUpdate = criacaoRacaRepository.findAll().collectList().block().size();
        criacaoRaca.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, criacaoRaca.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoRaca))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoRaca in the database
        List<CriacaoRaca> criacaoRacaList = criacaoRacaRepository.findAll().collectList().block();
        assertThat(criacaoRacaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCriacaoRaca() throws Exception {
        int databaseSizeBeforeUpdate = criacaoRacaRepository.findAll().collectList().block().size();
        criacaoRaca.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoRaca))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoRaca in the database
        List<CriacaoRaca> criacaoRacaList = criacaoRacaRepository.findAll().collectList().block();
        assertThat(criacaoRacaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCriacaoRaca() throws Exception {
        int databaseSizeBeforeUpdate = criacaoRacaRepository.findAll().collectList().block().size();
        criacaoRaca.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoRaca))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CriacaoRaca in the database
        List<CriacaoRaca> criacaoRacaList = criacaoRacaRepository.findAll().collectList().block();
        assertThat(criacaoRacaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCriacaoRacaWithPatch() throws Exception {
        // Initialize the database
        criacaoRacaRepository.save(criacaoRaca).block();

        int databaseSizeBeforeUpdate = criacaoRacaRepository.findAll().collectList().block().size();

        // Update the criacaoRaca using partial update
        CriacaoRaca partialUpdatedCriacaoRaca = new CriacaoRaca();
        partialUpdatedCriacaoRaca.setId(criacaoRaca.getId());

        partialUpdatedCriacaoRaca.ativo(UPDATED_ATIVO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCriacaoRaca.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCriacaoRaca))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CriacaoRaca in the database
        List<CriacaoRaca> criacaoRacaList = criacaoRacaRepository.findAll().collectList().block();
        assertThat(criacaoRacaList).hasSize(databaseSizeBeforeUpdate);
        CriacaoRaca testCriacaoRaca = criacaoRacaList.get(criacaoRacaList.size() - 1);
        assertThat(testCriacaoRaca.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testCriacaoRaca.getAtivo()).isEqualTo(UPDATED_ATIVO);
    }

    @Test
    void fullUpdateCriacaoRacaWithPatch() throws Exception {
        // Initialize the database
        criacaoRacaRepository.save(criacaoRaca).block();

        int databaseSizeBeforeUpdate = criacaoRacaRepository.findAll().collectList().block().size();

        // Update the criacaoRaca using partial update
        CriacaoRaca partialUpdatedCriacaoRaca = new CriacaoRaca();
        partialUpdatedCriacaoRaca.setId(criacaoRaca.getId());

        partialUpdatedCriacaoRaca.descricao(UPDATED_DESCRICAO).ativo(UPDATED_ATIVO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCriacaoRaca.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCriacaoRaca))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CriacaoRaca in the database
        List<CriacaoRaca> criacaoRacaList = criacaoRacaRepository.findAll().collectList().block();
        assertThat(criacaoRacaList).hasSize(databaseSizeBeforeUpdate);
        CriacaoRaca testCriacaoRaca = criacaoRacaList.get(criacaoRacaList.size() - 1);
        assertThat(testCriacaoRaca.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testCriacaoRaca.getAtivo()).isEqualTo(UPDATED_ATIVO);
    }

    @Test
    void patchNonExistingCriacaoRaca() throws Exception {
        int databaseSizeBeforeUpdate = criacaoRacaRepository.findAll().collectList().block().size();
        criacaoRaca.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, criacaoRaca.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoRaca))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoRaca in the database
        List<CriacaoRaca> criacaoRacaList = criacaoRacaRepository.findAll().collectList().block();
        assertThat(criacaoRacaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCriacaoRaca() throws Exception {
        int databaseSizeBeforeUpdate = criacaoRacaRepository.findAll().collectList().block().size();
        criacaoRaca.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoRaca))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoRaca in the database
        List<CriacaoRaca> criacaoRacaList = criacaoRacaRepository.findAll().collectList().block();
        assertThat(criacaoRacaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCriacaoRaca() throws Exception {
        int databaseSizeBeforeUpdate = criacaoRacaRepository.findAll().collectList().block().size();
        criacaoRaca.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoRaca))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CriacaoRaca in the database
        List<CriacaoRaca> criacaoRacaList = criacaoRacaRepository.findAll().collectList().block();
        assertThat(criacaoRacaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCriacaoRaca() {
        // Initialize the database
        criacaoRacaRepository.save(criacaoRaca).block();

        int databaseSizeBeforeDelete = criacaoRacaRepository.findAll().collectList().block().size();

        // Delete the criacaoRaca
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, criacaoRaca.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<CriacaoRaca> criacaoRacaList = criacaoRacaRepository.findAll().collectList().block();
        assertThat(criacaoRacaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
