package com.mboss.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mboss.core.IntegrationTest;
import com.mboss.core.domain.PessoaParentesco;
import com.mboss.core.repository.PessoaParentescoRepository;
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
 * Integration tests for the {@link PessoaParentescoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PessoaParentescoResourceIT {

    private static final String DEFAULT_CPF = "AAAAAAAAAA";
    private static final String UPDATED_CPF = "BBBBBBBBBB";

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/pessoa-parentescos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PessoaParentescoRepository pessoaParentescoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PessoaParentesco pessoaParentesco;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaParentesco createEntity(EntityManager em) {
        PessoaParentesco pessoaParentesco = new PessoaParentesco().cpf(DEFAULT_CPF).nome(DEFAULT_NOME).email(DEFAULT_EMAIL);
        return pessoaParentesco;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaParentesco createUpdatedEntity(EntityManager em) {
        PessoaParentesco pessoaParentesco = new PessoaParentesco().cpf(UPDATED_CPF).nome(UPDATED_NOME).email(UPDATED_EMAIL);
        return pessoaParentesco;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PessoaParentesco.class).block();
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
        pessoaParentesco = createEntity(em);
    }

    @Test
    void createPessoaParentesco() throws Exception {
        int databaseSizeBeforeCreate = pessoaParentescoRepository.findAll().collectList().block().size();
        // Create the PessoaParentesco
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaParentesco))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the PessoaParentesco in the database
        List<PessoaParentesco> pessoaParentescoList = pessoaParentescoRepository.findAll().collectList().block();
        assertThat(pessoaParentescoList).hasSize(databaseSizeBeforeCreate + 1);
        PessoaParentesco testPessoaParentesco = pessoaParentescoList.get(pessoaParentescoList.size() - 1);
        assertThat(testPessoaParentesco.getCpf()).isEqualTo(DEFAULT_CPF);
        assertThat(testPessoaParentesco.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testPessoaParentesco.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    void createPessoaParentescoWithExistingId() throws Exception {
        // Create the PessoaParentesco with an existing ID
        pessoaParentesco.setId(1L);

        int databaseSizeBeforeCreate = pessoaParentescoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaParentesco))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaParentesco in the database
        List<PessoaParentesco> pessoaParentescoList = pessoaParentescoRepository.findAll().collectList().block();
        assertThat(pessoaParentescoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPessoaParentescosAsStream() {
        // Initialize the database
        pessoaParentescoRepository.save(pessoaParentesco).block();

        List<PessoaParentesco> pessoaParentescoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PessoaParentesco.class)
            .getResponseBody()
            .filter(pessoaParentesco::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(pessoaParentescoList).isNotNull();
        assertThat(pessoaParentescoList).hasSize(1);
        PessoaParentesco testPessoaParentesco = pessoaParentescoList.get(0);
        assertThat(testPessoaParentesco.getCpf()).isEqualTo(DEFAULT_CPF);
        assertThat(testPessoaParentesco.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testPessoaParentesco.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    void getAllPessoaParentescos() {
        // Initialize the database
        pessoaParentescoRepository.save(pessoaParentesco).block();

        // Get all the pessoaParentescoList
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
            .value(hasItem(pessoaParentesco.getId().intValue()))
            .jsonPath("$.[*].cpf")
            .value(hasItem(DEFAULT_CPF))
            .jsonPath("$.[*].nome")
            .value(hasItem(DEFAULT_NOME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL));
    }

    @Test
    void getPessoaParentesco() {
        // Initialize the database
        pessoaParentescoRepository.save(pessoaParentesco).block();

        // Get the pessoaParentesco
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pessoaParentesco.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pessoaParentesco.getId().intValue()))
            .jsonPath("$.cpf")
            .value(is(DEFAULT_CPF))
            .jsonPath("$.nome")
            .value(is(DEFAULT_NOME))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL));
    }

    @Test
    void getNonExistingPessoaParentesco() {
        // Get the pessoaParentesco
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPessoaParentesco() throws Exception {
        // Initialize the database
        pessoaParentescoRepository.save(pessoaParentesco).block();

        int databaseSizeBeforeUpdate = pessoaParentescoRepository.findAll().collectList().block().size();

        // Update the pessoaParentesco
        PessoaParentesco updatedPessoaParentesco = pessoaParentescoRepository.findById(pessoaParentesco.getId()).block();
        updatedPessoaParentesco.cpf(UPDATED_CPF).nome(UPDATED_NOME).email(UPDATED_EMAIL);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPessoaParentesco.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPessoaParentesco))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaParentesco in the database
        List<PessoaParentesco> pessoaParentescoList = pessoaParentescoRepository.findAll().collectList().block();
        assertThat(pessoaParentescoList).hasSize(databaseSizeBeforeUpdate);
        PessoaParentesco testPessoaParentesco = pessoaParentescoList.get(pessoaParentescoList.size() - 1);
        assertThat(testPessoaParentesco.getCpf()).isEqualTo(UPDATED_CPF);
        assertThat(testPessoaParentesco.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testPessoaParentesco.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    void putNonExistingPessoaParentesco() throws Exception {
        int databaseSizeBeforeUpdate = pessoaParentescoRepository.findAll().collectList().block().size();
        pessoaParentesco.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pessoaParentesco.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaParentesco))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaParentesco in the database
        List<PessoaParentesco> pessoaParentescoList = pessoaParentescoRepository.findAll().collectList().block();
        assertThat(pessoaParentescoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPessoaParentesco() throws Exception {
        int databaseSizeBeforeUpdate = pessoaParentescoRepository.findAll().collectList().block().size();
        pessoaParentesco.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaParentesco))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaParentesco in the database
        List<PessoaParentesco> pessoaParentescoList = pessoaParentescoRepository.findAll().collectList().block();
        assertThat(pessoaParentescoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPessoaParentesco() throws Exception {
        int databaseSizeBeforeUpdate = pessoaParentescoRepository.findAll().collectList().block().size();
        pessoaParentesco.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaParentesco))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaParentesco in the database
        List<PessoaParentesco> pessoaParentescoList = pessoaParentescoRepository.findAll().collectList().block();
        assertThat(pessoaParentescoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePessoaParentescoWithPatch() throws Exception {
        // Initialize the database
        pessoaParentescoRepository.save(pessoaParentesco).block();

        int databaseSizeBeforeUpdate = pessoaParentescoRepository.findAll().collectList().block().size();

        // Update the pessoaParentesco using partial update
        PessoaParentesco partialUpdatedPessoaParentesco = new PessoaParentesco();
        partialUpdatedPessoaParentesco.setId(pessoaParentesco.getId());

        partialUpdatedPessoaParentesco.nome(UPDATED_NOME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaParentesco.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaParentesco))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaParentesco in the database
        List<PessoaParentesco> pessoaParentescoList = pessoaParentescoRepository.findAll().collectList().block();
        assertThat(pessoaParentescoList).hasSize(databaseSizeBeforeUpdate);
        PessoaParentesco testPessoaParentesco = pessoaParentescoList.get(pessoaParentescoList.size() - 1);
        assertThat(testPessoaParentesco.getCpf()).isEqualTo(DEFAULT_CPF);
        assertThat(testPessoaParentesco.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testPessoaParentesco.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    void fullUpdatePessoaParentescoWithPatch() throws Exception {
        // Initialize the database
        pessoaParentescoRepository.save(pessoaParentesco).block();

        int databaseSizeBeforeUpdate = pessoaParentescoRepository.findAll().collectList().block().size();

        // Update the pessoaParentesco using partial update
        PessoaParentesco partialUpdatedPessoaParentesco = new PessoaParentesco();
        partialUpdatedPessoaParentesco.setId(pessoaParentesco.getId());

        partialUpdatedPessoaParentesco.cpf(UPDATED_CPF).nome(UPDATED_NOME).email(UPDATED_EMAIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaParentesco.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaParentesco))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaParentesco in the database
        List<PessoaParentesco> pessoaParentescoList = pessoaParentescoRepository.findAll().collectList().block();
        assertThat(pessoaParentescoList).hasSize(databaseSizeBeforeUpdate);
        PessoaParentesco testPessoaParentesco = pessoaParentescoList.get(pessoaParentescoList.size() - 1);
        assertThat(testPessoaParentesco.getCpf()).isEqualTo(UPDATED_CPF);
        assertThat(testPessoaParentesco.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testPessoaParentesco.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    void patchNonExistingPessoaParentesco() throws Exception {
        int databaseSizeBeforeUpdate = pessoaParentescoRepository.findAll().collectList().block().size();
        pessoaParentesco.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pessoaParentesco.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaParentesco))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaParentesco in the database
        List<PessoaParentesco> pessoaParentescoList = pessoaParentescoRepository.findAll().collectList().block();
        assertThat(pessoaParentescoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPessoaParentesco() throws Exception {
        int databaseSizeBeforeUpdate = pessoaParentescoRepository.findAll().collectList().block().size();
        pessoaParentesco.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaParentesco))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaParentesco in the database
        List<PessoaParentesco> pessoaParentescoList = pessoaParentescoRepository.findAll().collectList().block();
        assertThat(pessoaParentescoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPessoaParentesco() throws Exception {
        int databaseSizeBeforeUpdate = pessoaParentescoRepository.findAll().collectList().block().size();
        pessoaParentesco.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaParentesco))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaParentesco in the database
        List<PessoaParentesco> pessoaParentescoList = pessoaParentescoRepository.findAll().collectList().block();
        assertThat(pessoaParentescoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePessoaParentesco() {
        // Initialize the database
        pessoaParentescoRepository.save(pessoaParentesco).block();

        int databaseSizeBeforeDelete = pessoaParentescoRepository.findAll().collectList().block().size();

        // Delete the pessoaParentesco
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pessoaParentesco.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<PessoaParentesco> pessoaParentescoList = pessoaParentescoRepository.findAll().collectList().block();
        assertThat(pessoaParentescoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
