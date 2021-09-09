package com.mboss.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mboss.core.IntegrationTest;
import com.mboss.core.domain.PessoaCriacao;
import com.mboss.core.repository.PessoaCriacaoRepository;
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
 * Integration tests for the {@link PessoaCriacaoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PessoaCriacaoResourceIT {

    private static final Long DEFAULT_ID_CRIACAO = 1L;
    private static final Long UPDATED_ID_CRIACAO = 2L;

    private static final String ENTITY_API_URL = "/api/pessoa-criacaos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PessoaCriacaoRepository pessoaCriacaoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PessoaCriacao pessoaCriacao;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaCriacao createEntity(EntityManager em) {
        PessoaCriacao pessoaCriacao = new PessoaCriacao().idCriacao(DEFAULT_ID_CRIACAO);
        return pessoaCriacao;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaCriacao createUpdatedEntity(EntityManager em) {
        PessoaCriacao pessoaCriacao = new PessoaCriacao().idCriacao(UPDATED_ID_CRIACAO);
        return pessoaCriacao;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PessoaCriacao.class).block();
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
        pessoaCriacao = createEntity(em);
    }

    @Test
    void createPessoaCriacao() throws Exception {
        int databaseSizeBeforeCreate = pessoaCriacaoRepository.findAll().collectList().block().size();
        // Create the PessoaCriacao
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaCriacao))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the PessoaCriacao in the database
        List<PessoaCriacao> pessoaCriacaoList = pessoaCriacaoRepository.findAll().collectList().block();
        assertThat(pessoaCriacaoList).hasSize(databaseSizeBeforeCreate + 1);
        PessoaCriacao testPessoaCriacao = pessoaCriacaoList.get(pessoaCriacaoList.size() - 1);
        assertThat(testPessoaCriacao.getIdCriacao()).isEqualTo(DEFAULT_ID_CRIACAO);
    }

    @Test
    void createPessoaCriacaoWithExistingId() throws Exception {
        // Create the PessoaCriacao with an existing ID
        pessoaCriacao.setId(1L);

        int databaseSizeBeforeCreate = pessoaCriacaoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaCriacao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaCriacao in the database
        List<PessoaCriacao> pessoaCriacaoList = pessoaCriacaoRepository.findAll().collectList().block();
        assertThat(pessoaCriacaoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPessoaCriacaosAsStream() {
        // Initialize the database
        pessoaCriacaoRepository.save(pessoaCriacao).block();

        List<PessoaCriacao> pessoaCriacaoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PessoaCriacao.class)
            .getResponseBody()
            .filter(pessoaCriacao::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(pessoaCriacaoList).isNotNull();
        assertThat(pessoaCriacaoList).hasSize(1);
        PessoaCriacao testPessoaCriacao = pessoaCriacaoList.get(0);
        assertThat(testPessoaCriacao.getIdCriacao()).isEqualTo(DEFAULT_ID_CRIACAO);
    }

    @Test
    void getAllPessoaCriacaos() {
        // Initialize the database
        pessoaCriacaoRepository.save(pessoaCriacao).block();

        // Get all the pessoaCriacaoList
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
            .value(hasItem(pessoaCriacao.getId().intValue()))
            .jsonPath("$.[*].idCriacao")
            .value(hasItem(DEFAULT_ID_CRIACAO.intValue()));
    }

    @Test
    void getPessoaCriacao() {
        // Initialize the database
        pessoaCriacaoRepository.save(pessoaCriacao).block();

        // Get the pessoaCriacao
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pessoaCriacao.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pessoaCriacao.getId().intValue()))
            .jsonPath("$.idCriacao")
            .value(is(DEFAULT_ID_CRIACAO.intValue()));
    }

    @Test
    void getNonExistingPessoaCriacao() {
        // Get the pessoaCriacao
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPessoaCriacao() throws Exception {
        // Initialize the database
        pessoaCriacaoRepository.save(pessoaCriacao).block();

        int databaseSizeBeforeUpdate = pessoaCriacaoRepository.findAll().collectList().block().size();

        // Update the pessoaCriacao
        PessoaCriacao updatedPessoaCriacao = pessoaCriacaoRepository.findById(pessoaCriacao.getId()).block();
        updatedPessoaCriacao.idCriacao(UPDATED_ID_CRIACAO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPessoaCriacao.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPessoaCriacao))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaCriacao in the database
        List<PessoaCriacao> pessoaCriacaoList = pessoaCriacaoRepository.findAll().collectList().block();
        assertThat(pessoaCriacaoList).hasSize(databaseSizeBeforeUpdate);
        PessoaCriacao testPessoaCriacao = pessoaCriacaoList.get(pessoaCriacaoList.size() - 1);
        assertThat(testPessoaCriacao.getIdCriacao()).isEqualTo(UPDATED_ID_CRIACAO);
    }

    @Test
    void putNonExistingPessoaCriacao() throws Exception {
        int databaseSizeBeforeUpdate = pessoaCriacaoRepository.findAll().collectList().block().size();
        pessoaCriacao.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pessoaCriacao.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaCriacao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaCriacao in the database
        List<PessoaCriacao> pessoaCriacaoList = pessoaCriacaoRepository.findAll().collectList().block();
        assertThat(pessoaCriacaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPessoaCriacao() throws Exception {
        int databaseSizeBeforeUpdate = pessoaCriacaoRepository.findAll().collectList().block().size();
        pessoaCriacao.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaCriacao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaCriacao in the database
        List<PessoaCriacao> pessoaCriacaoList = pessoaCriacaoRepository.findAll().collectList().block();
        assertThat(pessoaCriacaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPessoaCriacao() throws Exception {
        int databaseSizeBeforeUpdate = pessoaCriacaoRepository.findAll().collectList().block().size();
        pessoaCriacao.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaCriacao))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaCriacao in the database
        List<PessoaCriacao> pessoaCriacaoList = pessoaCriacaoRepository.findAll().collectList().block();
        assertThat(pessoaCriacaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePessoaCriacaoWithPatch() throws Exception {
        // Initialize the database
        pessoaCriacaoRepository.save(pessoaCriacao).block();

        int databaseSizeBeforeUpdate = pessoaCriacaoRepository.findAll().collectList().block().size();

        // Update the pessoaCriacao using partial update
        PessoaCriacao partialUpdatedPessoaCriacao = new PessoaCriacao();
        partialUpdatedPessoaCriacao.setId(pessoaCriacao.getId());

        partialUpdatedPessoaCriacao.idCriacao(UPDATED_ID_CRIACAO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaCriacao.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaCriacao))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaCriacao in the database
        List<PessoaCriacao> pessoaCriacaoList = pessoaCriacaoRepository.findAll().collectList().block();
        assertThat(pessoaCriacaoList).hasSize(databaseSizeBeforeUpdate);
        PessoaCriacao testPessoaCriacao = pessoaCriacaoList.get(pessoaCriacaoList.size() - 1);
        assertThat(testPessoaCriacao.getIdCriacao()).isEqualTo(UPDATED_ID_CRIACAO);
    }

    @Test
    void fullUpdatePessoaCriacaoWithPatch() throws Exception {
        // Initialize the database
        pessoaCriacaoRepository.save(pessoaCriacao).block();

        int databaseSizeBeforeUpdate = pessoaCriacaoRepository.findAll().collectList().block().size();

        // Update the pessoaCriacao using partial update
        PessoaCriacao partialUpdatedPessoaCriacao = new PessoaCriacao();
        partialUpdatedPessoaCriacao.setId(pessoaCriacao.getId());

        partialUpdatedPessoaCriacao.idCriacao(UPDATED_ID_CRIACAO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaCriacao.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaCriacao))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaCriacao in the database
        List<PessoaCriacao> pessoaCriacaoList = pessoaCriacaoRepository.findAll().collectList().block();
        assertThat(pessoaCriacaoList).hasSize(databaseSizeBeforeUpdate);
        PessoaCriacao testPessoaCriacao = pessoaCriacaoList.get(pessoaCriacaoList.size() - 1);
        assertThat(testPessoaCriacao.getIdCriacao()).isEqualTo(UPDATED_ID_CRIACAO);
    }

    @Test
    void patchNonExistingPessoaCriacao() throws Exception {
        int databaseSizeBeforeUpdate = pessoaCriacaoRepository.findAll().collectList().block().size();
        pessoaCriacao.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pessoaCriacao.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaCriacao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaCriacao in the database
        List<PessoaCriacao> pessoaCriacaoList = pessoaCriacaoRepository.findAll().collectList().block();
        assertThat(pessoaCriacaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPessoaCriacao() throws Exception {
        int databaseSizeBeforeUpdate = pessoaCriacaoRepository.findAll().collectList().block().size();
        pessoaCriacao.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaCriacao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaCriacao in the database
        List<PessoaCriacao> pessoaCriacaoList = pessoaCriacaoRepository.findAll().collectList().block();
        assertThat(pessoaCriacaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPessoaCriacao() throws Exception {
        int databaseSizeBeforeUpdate = pessoaCriacaoRepository.findAll().collectList().block().size();
        pessoaCriacao.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaCriacao))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaCriacao in the database
        List<PessoaCriacao> pessoaCriacaoList = pessoaCriacaoRepository.findAll().collectList().block();
        assertThat(pessoaCriacaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePessoaCriacao() {
        // Initialize the database
        pessoaCriacaoRepository.save(pessoaCriacao).block();

        int databaseSizeBeforeDelete = pessoaCriacaoRepository.findAll().collectList().block().size();

        // Delete the pessoaCriacao
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pessoaCriacao.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<PessoaCriacao> pessoaCriacaoList = pessoaCriacaoRepository.findAll().collectList().block();
        assertThat(pessoaCriacaoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
