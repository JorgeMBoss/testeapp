package com.mboss.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mboss.core.IntegrationTest;
import com.mboss.core.domain.PessoaMedico;
import com.mboss.core.repository.PessoaMedicoRepository;
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
 * Integration tests for the {@link PessoaMedicoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PessoaMedicoResourceIT {

    private static final String DEFAULT_CRM = "AAAAAAAAAA";
    private static final String UPDATED_CRM = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/pessoa-medicos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PessoaMedicoRepository pessoaMedicoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PessoaMedico pessoaMedico;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaMedico createEntity(EntityManager em) {
        PessoaMedico pessoaMedico = new PessoaMedico().crm(DEFAULT_CRM);
        return pessoaMedico;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaMedico createUpdatedEntity(EntityManager em) {
        PessoaMedico pessoaMedico = new PessoaMedico().crm(UPDATED_CRM);
        return pessoaMedico;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PessoaMedico.class).block();
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
        pessoaMedico = createEntity(em);
    }

    @Test
    void createPessoaMedico() throws Exception {
        int databaseSizeBeforeCreate = pessoaMedicoRepository.findAll().collectList().block().size();
        // Create the PessoaMedico
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaMedico))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the PessoaMedico in the database
        List<PessoaMedico> pessoaMedicoList = pessoaMedicoRepository.findAll().collectList().block();
        assertThat(pessoaMedicoList).hasSize(databaseSizeBeforeCreate + 1);
        PessoaMedico testPessoaMedico = pessoaMedicoList.get(pessoaMedicoList.size() - 1);
        assertThat(testPessoaMedico.getCrm()).isEqualTo(DEFAULT_CRM);
    }

    @Test
    void createPessoaMedicoWithExistingId() throws Exception {
        // Create the PessoaMedico with an existing ID
        pessoaMedico.setId(1L);

        int databaseSizeBeforeCreate = pessoaMedicoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaMedico))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaMedico in the database
        List<PessoaMedico> pessoaMedicoList = pessoaMedicoRepository.findAll().collectList().block();
        assertThat(pessoaMedicoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkCrmIsRequired() throws Exception {
        int databaseSizeBeforeTest = pessoaMedicoRepository.findAll().collectList().block().size();
        // set the field null
        pessoaMedico.setCrm(null);

        // Create the PessoaMedico, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaMedico))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<PessoaMedico> pessoaMedicoList = pessoaMedicoRepository.findAll().collectList().block();
        assertThat(pessoaMedicoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllPessoaMedicosAsStream() {
        // Initialize the database
        pessoaMedicoRepository.save(pessoaMedico).block();

        List<PessoaMedico> pessoaMedicoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PessoaMedico.class)
            .getResponseBody()
            .filter(pessoaMedico::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(pessoaMedicoList).isNotNull();
        assertThat(pessoaMedicoList).hasSize(1);
        PessoaMedico testPessoaMedico = pessoaMedicoList.get(0);
        assertThat(testPessoaMedico.getCrm()).isEqualTo(DEFAULT_CRM);
    }

    @Test
    void getAllPessoaMedicos() {
        // Initialize the database
        pessoaMedicoRepository.save(pessoaMedico).block();

        // Get all the pessoaMedicoList
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
            .value(hasItem(pessoaMedico.getId().intValue()))
            .jsonPath("$.[*].crm")
            .value(hasItem(DEFAULT_CRM));
    }

    @Test
    void getPessoaMedico() {
        // Initialize the database
        pessoaMedicoRepository.save(pessoaMedico).block();

        // Get the pessoaMedico
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pessoaMedico.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pessoaMedico.getId().intValue()))
            .jsonPath("$.crm")
            .value(is(DEFAULT_CRM));
    }

    @Test
    void getNonExistingPessoaMedico() {
        // Get the pessoaMedico
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPessoaMedico() throws Exception {
        // Initialize the database
        pessoaMedicoRepository.save(pessoaMedico).block();

        int databaseSizeBeforeUpdate = pessoaMedicoRepository.findAll().collectList().block().size();

        // Update the pessoaMedico
        PessoaMedico updatedPessoaMedico = pessoaMedicoRepository.findById(pessoaMedico.getId()).block();
        updatedPessoaMedico.crm(UPDATED_CRM);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPessoaMedico.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPessoaMedico))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaMedico in the database
        List<PessoaMedico> pessoaMedicoList = pessoaMedicoRepository.findAll().collectList().block();
        assertThat(pessoaMedicoList).hasSize(databaseSizeBeforeUpdate);
        PessoaMedico testPessoaMedico = pessoaMedicoList.get(pessoaMedicoList.size() - 1);
        assertThat(testPessoaMedico.getCrm()).isEqualTo(UPDATED_CRM);
    }

    @Test
    void putNonExistingPessoaMedico() throws Exception {
        int databaseSizeBeforeUpdate = pessoaMedicoRepository.findAll().collectList().block().size();
        pessoaMedico.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pessoaMedico.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaMedico))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaMedico in the database
        List<PessoaMedico> pessoaMedicoList = pessoaMedicoRepository.findAll().collectList().block();
        assertThat(pessoaMedicoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPessoaMedico() throws Exception {
        int databaseSizeBeforeUpdate = pessoaMedicoRepository.findAll().collectList().block().size();
        pessoaMedico.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaMedico))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaMedico in the database
        List<PessoaMedico> pessoaMedicoList = pessoaMedicoRepository.findAll().collectList().block();
        assertThat(pessoaMedicoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPessoaMedico() throws Exception {
        int databaseSizeBeforeUpdate = pessoaMedicoRepository.findAll().collectList().block().size();
        pessoaMedico.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaMedico))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaMedico in the database
        List<PessoaMedico> pessoaMedicoList = pessoaMedicoRepository.findAll().collectList().block();
        assertThat(pessoaMedicoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePessoaMedicoWithPatch() throws Exception {
        // Initialize the database
        pessoaMedicoRepository.save(pessoaMedico).block();

        int databaseSizeBeforeUpdate = pessoaMedicoRepository.findAll().collectList().block().size();

        // Update the pessoaMedico using partial update
        PessoaMedico partialUpdatedPessoaMedico = new PessoaMedico();
        partialUpdatedPessoaMedico.setId(pessoaMedico.getId());

        partialUpdatedPessoaMedico.crm(UPDATED_CRM);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaMedico.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaMedico))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaMedico in the database
        List<PessoaMedico> pessoaMedicoList = pessoaMedicoRepository.findAll().collectList().block();
        assertThat(pessoaMedicoList).hasSize(databaseSizeBeforeUpdate);
        PessoaMedico testPessoaMedico = pessoaMedicoList.get(pessoaMedicoList.size() - 1);
        assertThat(testPessoaMedico.getCrm()).isEqualTo(UPDATED_CRM);
    }

    @Test
    void fullUpdatePessoaMedicoWithPatch() throws Exception {
        // Initialize the database
        pessoaMedicoRepository.save(pessoaMedico).block();

        int databaseSizeBeforeUpdate = pessoaMedicoRepository.findAll().collectList().block().size();

        // Update the pessoaMedico using partial update
        PessoaMedico partialUpdatedPessoaMedico = new PessoaMedico();
        partialUpdatedPessoaMedico.setId(pessoaMedico.getId());

        partialUpdatedPessoaMedico.crm(UPDATED_CRM);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaMedico.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaMedico))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaMedico in the database
        List<PessoaMedico> pessoaMedicoList = pessoaMedicoRepository.findAll().collectList().block();
        assertThat(pessoaMedicoList).hasSize(databaseSizeBeforeUpdate);
        PessoaMedico testPessoaMedico = pessoaMedicoList.get(pessoaMedicoList.size() - 1);
        assertThat(testPessoaMedico.getCrm()).isEqualTo(UPDATED_CRM);
    }

    @Test
    void patchNonExistingPessoaMedico() throws Exception {
        int databaseSizeBeforeUpdate = pessoaMedicoRepository.findAll().collectList().block().size();
        pessoaMedico.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pessoaMedico.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaMedico))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaMedico in the database
        List<PessoaMedico> pessoaMedicoList = pessoaMedicoRepository.findAll().collectList().block();
        assertThat(pessoaMedicoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPessoaMedico() throws Exception {
        int databaseSizeBeforeUpdate = pessoaMedicoRepository.findAll().collectList().block().size();
        pessoaMedico.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaMedico))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaMedico in the database
        List<PessoaMedico> pessoaMedicoList = pessoaMedicoRepository.findAll().collectList().block();
        assertThat(pessoaMedicoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPessoaMedico() throws Exception {
        int databaseSizeBeforeUpdate = pessoaMedicoRepository.findAll().collectList().block().size();
        pessoaMedico.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaMedico))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaMedico in the database
        List<PessoaMedico> pessoaMedicoList = pessoaMedicoRepository.findAll().collectList().block();
        assertThat(pessoaMedicoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePessoaMedico() {
        // Initialize the database
        pessoaMedicoRepository.save(pessoaMedico).block();

        int databaseSizeBeforeDelete = pessoaMedicoRepository.findAll().collectList().block().size();

        // Delete the pessoaMedico
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pessoaMedico.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<PessoaMedico> pessoaMedicoList = pessoaMedicoRepository.findAll().collectList().block();
        assertThat(pessoaMedicoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
