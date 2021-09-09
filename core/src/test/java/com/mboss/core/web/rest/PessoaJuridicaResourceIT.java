package com.mboss.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mboss.core.IntegrationTest;
import com.mboss.core.domain.PessoaJuridica;
import com.mboss.core.repository.PessoaJuridicaRepository;
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
 * Integration tests for the {@link PessoaJuridicaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PessoaJuridicaResourceIT {

    private static final String DEFAULT_CNPJ = "AAAAAAAAAAAAAA";
    private static final String UPDATED_CNPJ = "BBBBBBBBBBBBBB";

    private static final String DEFAULT_NOME_RAZAO = "AAAAAAAAAA";
    private static final String UPDATED_NOME_RAZAO = "BBBBBBBBBB";

    private static final String DEFAULT_NOME_FANTASIA = "AAAAAAAAAA";
    private static final String UPDATED_NOME_FANTASIA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/pessoa-juridicas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PessoaJuridicaRepository pessoaJuridicaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PessoaJuridica pessoaJuridica;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaJuridica createEntity(EntityManager em) {
        PessoaJuridica pessoaJuridica = new PessoaJuridica()
            .cnpj(DEFAULT_CNPJ)
            .nomeRazao(DEFAULT_NOME_RAZAO)
            .nomeFantasia(DEFAULT_NOME_FANTASIA);
        return pessoaJuridica;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaJuridica createUpdatedEntity(EntityManager em) {
        PessoaJuridica pessoaJuridica = new PessoaJuridica()
            .cnpj(UPDATED_CNPJ)
            .nomeRazao(UPDATED_NOME_RAZAO)
            .nomeFantasia(UPDATED_NOME_FANTASIA);
        return pessoaJuridica;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PessoaJuridica.class).block();
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
        pessoaJuridica = createEntity(em);
    }

    @Test
    void createPessoaJuridica() throws Exception {
        int databaseSizeBeforeCreate = pessoaJuridicaRepository.findAll().collectList().block().size();
        // Create the PessoaJuridica
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaJuridica))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the PessoaJuridica in the database
        List<PessoaJuridica> pessoaJuridicaList = pessoaJuridicaRepository.findAll().collectList().block();
        assertThat(pessoaJuridicaList).hasSize(databaseSizeBeforeCreate + 1);
        PessoaJuridica testPessoaJuridica = pessoaJuridicaList.get(pessoaJuridicaList.size() - 1);
        assertThat(testPessoaJuridica.getCnpj()).isEqualTo(DEFAULT_CNPJ);
        assertThat(testPessoaJuridica.getNomeRazao()).isEqualTo(DEFAULT_NOME_RAZAO);
        assertThat(testPessoaJuridica.getNomeFantasia()).isEqualTo(DEFAULT_NOME_FANTASIA);
    }

    @Test
    void createPessoaJuridicaWithExistingId() throws Exception {
        // Create the PessoaJuridica with an existing ID
        pessoaJuridica.setId(1L);

        int databaseSizeBeforeCreate = pessoaJuridicaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaJuridica))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaJuridica in the database
        List<PessoaJuridica> pessoaJuridicaList = pessoaJuridicaRepository.findAll().collectList().block();
        assertThat(pessoaJuridicaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPessoaJuridicasAsStream() {
        // Initialize the database
        pessoaJuridicaRepository.save(pessoaJuridica).block();

        List<PessoaJuridica> pessoaJuridicaList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PessoaJuridica.class)
            .getResponseBody()
            .filter(pessoaJuridica::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(pessoaJuridicaList).isNotNull();
        assertThat(pessoaJuridicaList).hasSize(1);
        PessoaJuridica testPessoaJuridica = pessoaJuridicaList.get(0);
        assertThat(testPessoaJuridica.getCnpj()).isEqualTo(DEFAULT_CNPJ);
        assertThat(testPessoaJuridica.getNomeRazao()).isEqualTo(DEFAULT_NOME_RAZAO);
        assertThat(testPessoaJuridica.getNomeFantasia()).isEqualTo(DEFAULT_NOME_FANTASIA);
    }

    @Test
    void getAllPessoaJuridicas() {
        // Initialize the database
        pessoaJuridicaRepository.save(pessoaJuridica).block();

        // Get all the pessoaJuridicaList
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
            .value(hasItem(pessoaJuridica.getId().intValue()))
            .jsonPath("$.[*].cnpj")
            .value(hasItem(DEFAULT_CNPJ))
            .jsonPath("$.[*].nomeRazao")
            .value(hasItem(DEFAULT_NOME_RAZAO))
            .jsonPath("$.[*].nomeFantasia")
            .value(hasItem(DEFAULT_NOME_FANTASIA));
    }

    @Test
    void getPessoaJuridica() {
        // Initialize the database
        pessoaJuridicaRepository.save(pessoaJuridica).block();

        // Get the pessoaJuridica
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pessoaJuridica.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pessoaJuridica.getId().intValue()))
            .jsonPath("$.cnpj")
            .value(is(DEFAULT_CNPJ))
            .jsonPath("$.nomeRazao")
            .value(is(DEFAULT_NOME_RAZAO))
            .jsonPath("$.nomeFantasia")
            .value(is(DEFAULT_NOME_FANTASIA));
    }

    @Test
    void getNonExistingPessoaJuridica() {
        // Get the pessoaJuridica
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPessoaJuridica() throws Exception {
        // Initialize the database
        pessoaJuridicaRepository.save(pessoaJuridica).block();

        int databaseSizeBeforeUpdate = pessoaJuridicaRepository.findAll().collectList().block().size();

        // Update the pessoaJuridica
        PessoaJuridica updatedPessoaJuridica = pessoaJuridicaRepository.findById(pessoaJuridica.getId()).block();
        updatedPessoaJuridica.cnpj(UPDATED_CNPJ).nomeRazao(UPDATED_NOME_RAZAO).nomeFantasia(UPDATED_NOME_FANTASIA);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPessoaJuridica.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPessoaJuridica))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaJuridica in the database
        List<PessoaJuridica> pessoaJuridicaList = pessoaJuridicaRepository.findAll().collectList().block();
        assertThat(pessoaJuridicaList).hasSize(databaseSizeBeforeUpdate);
        PessoaJuridica testPessoaJuridica = pessoaJuridicaList.get(pessoaJuridicaList.size() - 1);
        assertThat(testPessoaJuridica.getCnpj()).isEqualTo(UPDATED_CNPJ);
        assertThat(testPessoaJuridica.getNomeRazao()).isEqualTo(UPDATED_NOME_RAZAO);
        assertThat(testPessoaJuridica.getNomeFantasia()).isEqualTo(UPDATED_NOME_FANTASIA);
    }

    @Test
    void putNonExistingPessoaJuridica() throws Exception {
        int databaseSizeBeforeUpdate = pessoaJuridicaRepository.findAll().collectList().block().size();
        pessoaJuridica.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pessoaJuridica.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaJuridica))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaJuridica in the database
        List<PessoaJuridica> pessoaJuridicaList = pessoaJuridicaRepository.findAll().collectList().block();
        assertThat(pessoaJuridicaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPessoaJuridica() throws Exception {
        int databaseSizeBeforeUpdate = pessoaJuridicaRepository.findAll().collectList().block().size();
        pessoaJuridica.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaJuridica))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaJuridica in the database
        List<PessoaJuridica> pessoaJuridicaList = pessoaJuridicaRepository.findAll().collectList().block();
        assertThat(pessoaJuridicaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPessoaJuridica() throws Exception {
        int databaseSizeBeforeUpdate = pessoaJuridicaRepository.findAll().collectList().block().size();
        pessoaJuridica.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaJuridica))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaJuridica in the database
        List<PessoaJuridica> pessoaJuridicaList = pessoaJuridicaRepository.findAll().collectList().block();
        assertThat(pessoaJuridicaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePessoaJuridicaWithPatch() throws Exception {
        // Initialize the database
        pessoaJuridicaRepository.save(pessoaJuridica).block();

        int databaseSizeBeforeUpdate = pessoaJuridicaRepository.findAll().collectList().block().size();

        // Update the pessoaJuridica using partial update
        PessoaJuridica partialUpdatedPessoaJuridica = new PessoaJuridica();
        partialUpdatedPessoaJuridica.setId(pessoaJuridica.getId());

        partialUpdatedPessoaJuridica.nomeRazao(UPDATED_NOME_RAZAO).nomeFantasia(UPDATED_NOME_FANTASIA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaJuridica.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaJuridica))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaJuridica in the database
        List<PessoaJuridica> pessoaJuridicaList = pessoaJuridicaRepository.findAll().collectList().block();
        assertThat(pessoaJuridicaList).hasSize(databaseSizeBeforeUpdate);
        PessoaJuridica testPessoaJuridica = pessoaJuridicaList.get(pessoaJuridicaList.size() - 1);
        assertThat(testPessoaJuridica.getCnpj()).isEqualTo(DEFAULT_CNPJ);
        assertThat(testPessoaJuridica.getNomeRazao()).isEqualTo(UPDATED_NOME_RAZAO);
        assertThat(testPessoaJuridica.getNomeFantasia()).isEqualTo(UPDATED_NOME_FANTASIA);
    }

    @Test
    void fullUpdatePessoaJuridicaWithPatch() throws Exception {
        // Initialize the database
        pessoaJuridicaRepository.save(pessoaJuridica).block();

        int databaseSizeBeforeUpdate = pessoaJuridicaRepository.findAll().collectList().block().size();

        // Update the pessoaJuridica using partial update
        PessoaJuridica partialUpdatedPessoaJuridica = new PessoaJuridica();
        partialUpdatedPessoaJuridica.setId(pessoaJuridica.getId());

        partialUpdatedPessoaJuridica.cnpj(UPDATED_CNPJ).nomeRazao(UPDATED_NOME_RAZAO).nomeFantasia(UPDATED_NOME_FANTASIA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaJuridica.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaJuridica))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaJuridica in the database
        List<PessoaJuridica> pessoaJuridicaList = pessoaJuridicaRepository.findAll().collectList().block();
        assertThat(pessoaJuridicaList).hasSize(databaseSizeBeforeUpdate);
        PessoaJuridica testPessoaJuridica = pessoaJuridicaList.get(pessoaJuridicaList.size() - 1);
        assertThat(testPessoaJuridica.getCnpj()).isEqualTo(UPDATED_CNPJ);
        assertThat(testPessoaJuridica.getNomeRazao()).isEqualTo(UPDATED_NOME_RAZAO);
        assertThat(testPessoaJuridica.getNomeFantasia()).isEqualTo(UPDATED_NOME_FANTASIA);
    }

    @Test
    void patchNonExistingPessoaJuridica() throws Exception {
        int databaseSizeBeforeUpdate = pessoaJuridicaRepository.findAll().collectList().block().size();
        pessoaJuridica.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pessoaJuridica.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaJuridica))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaJuridica in the database
        List<PessoaJuridica> pessoaJuridicaList = pessoaJuridicaRepository.findAll().collectList().block();
        assertThat(pessoaJuridicaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPessoaJuridica() throws Exception {
        int databaseSizeBeforeUpdate = pessoaJuridicaRepository.findAll().collectList().block().size();
        pessoaJuridica.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaJuridica))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaJuridica in the database
        List<PessoaJuridica> pessoaJuridicaList = pessoaJuridicaRepository.findAll().collectList().block();
        assertThat(pessoaJuridicaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPessoaJuridica() throws Exception {
        int databaseSizeBeforeUpdate = pessoaJuridicaRepository.findAll().collectList().block().size();
        pessoaJuridica.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaJuridica))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaJuridica in the database
        List<PessoaJuridica> pessoaJuridicaList = pessoaJuridicaRepository.findAll().collectList().block();
        assertThat(pessoaJuridicaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePessoaJuridica() {
        // Initialize the database
        pessoaJuridicaRepository.save(pessoaJuridica).block();

        int databaseSizeBeforeDelete = pessoaJuridicaRepository.findAll().collectList().block().size();

        // Delete the pessoaJuridica
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pessoaJuridica.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<PessoaJuridica> pessoaJuridicaList = pessoaJuridicaRepository.findAll().collectList().block();
        assertThat(pessoaJuridicaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
