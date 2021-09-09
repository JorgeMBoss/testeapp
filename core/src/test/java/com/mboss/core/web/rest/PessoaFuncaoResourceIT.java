package com.mboss.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mboss.core.IntegrationTest;
import com.mboss.core.domain.PessoaFuncao;
import com.mboss.core.repository.PessoaFuncaoRepository;
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
 * Integration tests for the {@link PessoaFuncaoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PessoaFuncaoResourceIT {

    private static final String DEFAULT_DESCRICAO = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ATIVO = false;
    private static final Boolean UPDATED_ATIVO = true;

    private static final String ENTITY_API_URL = "/api/pessoa-funcaos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PessoaFuncaoRepository pessoaFuncaoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PessoaFuncao pessoaFuncao;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaFuncao createEntity(EntityManager em) {
        PessoaFuncao pessoaFuncao = new PessoaFuncao().descricao(DEFAULT_DESCRICAO).ativo(DEFAULT_ATIVO);
        return pessoaFuncao;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaFuncao createUpdatedEntity(EntityManager em) {
        PessoaFuncao pessoaFuncao = new PessoaFuncao().descricao(UPDATED_DESCRICAO).ativo(UPDATED_ATIVO);
        return pessoaFuncao;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PessoaFuncao.class).block();
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
        pessoaFuncao = createEntity(em);
    }

    @Test
    void createPessoaFuncao() throws Exception {
        int databaseSizeBeforeCreate = pessoaFuncaoRepository.findAll().collectList().block().size();
        // Create the PessoaFuncao
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFuncao))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the PessoaFuncao in the database
        List<PessoaFuncao> pessoaFuncaoList = pessoaFuncaoRepository.findAll().collectList().block();
        assertThat(pessoaFuncaoList).hasSize(databaseSizeBeforeCreate + 1);
        PessoaFuncao testPessoaFuncao = pessoaFuncaoList.get(pessoaFuncaoList.size() - 1);
        assertThat(testPessoaFuncao.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testPessoaFuncao.getAtivo()).isEqualTo(DEFAULT_ATIVO);
    }

    @Test
    void createPessoaFuncaoWithExistingId() throws Exception {
        // Create the PessoaFuncao with an existing ID
        pessoaFuncao.setId(1L);

        int databaseSizeBeforeCreate = pessoaFuncaoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFuncao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaFuncao in the database
        List<PessoaFuncao> pessoaFuncaoList = pessoaFuncaoRepository.findAll().collectList().block();
        assertThat(pessoaFuncaoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkDescricaoIsRequired() throws Exception {
        int databaseSizeBeforeTest = pessoaFuncaoRepository.findAll().collectList().block().size();
        // set the field null
        pessoaFuncao.setDescricao(null);

        // Create the PessoaFuncao, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFuncao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<PessoaFuncao> pessoaFuncaoList = pessoaFuncaoRepository.findAll().collectList().block();
        assertThat(pessoaFuncaoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllPessoaFuncaosAsStream() {
        // Initialize the database
        pessoaFuncaoRepository.save(pessoaFuncao).block();

        List<PessoaFuncao> pessoaFuncaoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PessoaFuncao.class)
            .getResponseBody()
            .filter(pessoaFuncao::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(pessoaFuncaoList).isNotNull();
        assertThat(pessoaFuncaoList).hasSize(1);
        PessoaFuncao testPessoaFuncao = pessoaFuncaoList.get(0);
        assertThat(testPessoaFuncao.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testPessoaFuncao.getAtivo()).isEqualTo(DEFAULT_ATIVO);
    }

    @Test
    void getAllPessoaFuncaos() {
        // Initialize the database
        pessoaFuncaoRepository.save(pessoaFuncao).block();

        // Get all the pessoaFuncaoList
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
            .value(hasItem(pessoaFuncao.getId().intValue()))
            .jsonPath("$.[*].descricao")
            .value(hasItem(DEFAULT_DESCRICAO))
            .jsonPath("$.[*].ativo")
            .value(hasItem(DEFAULT_ATIVO.booleanValue()));
    }

    @Test
    void getPessoaFuncao() {
        // Initialize the database
        pessoaFuncaoRepository.save(pessoaFuncao).block();

        // Get the pessoaFuncao
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pessoaFuncao.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pessoaFuncao.getId().intValue()))
            .jsonPath("$.descricao")
            .value(is(DEFAULT_DESCRICAO))
            .jsonPath("$.ativo")
            .value(is(DEFAULT_ATIVO.booleanValue()));
    }

    @Test
    void getNonExistingPessoaFuncao() {
        // Get the pessoaFuncao
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPessoaFuncao() throws Exception {
        // Initialize the database
        pessoaFuncaoRepository.save(pessoaFuncao).block();

        int databaseSizeBeforeUpdate = pessoaFuncaoRepository.findAll().collectList().block().size();

        // Update the pessoaFuncao
        PessoaFuncao updatedPessoaFuncao = pessoaFuncaoRepository.findById(pessoaFuncao.getId()).block();
        updatedPessoaFuncao.descricao(UPDATED_DESCRICAO).ativo(UPDATED_ATIVO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPessoaFuncao.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPessoaFuncao))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaFuncao in the database
        List<PessoaFuncao> pessoaFuncaoList = pessoaFuncaoRepository.findAll().collectList().block();
        assertThat(pessoaFuncaoList).hasSize(databaseSizeBeforeUpdate);
        PessoaFuncao testPessoaFuncao = pessoaFuncaoList.get(pessoaFuncaoList.size() - 1);
        assertThat(testPessoaFuncao.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testPessoaFuncao.getAtivo()).isEqualTo(UPDATED_ATIVO);
    }

    @Test
    void putNonExistingPessoaFuncao() throws Exception {
        int databaseSizeBeforeUpdate = pessoaFuncaoRepository.findAll().collectList().block().size();
        pessoaFuncao.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pessoaFuncao.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFuncao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaFuncao in the database
        List<PessoaFuncao> pessoaFuncaoList = pessoaFuncaoRepository.findAll().collectList().block();
        assertThat(pessoaFuncaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPessoaFuncao() throws Exception {
        int databaseSizeBeforeUpdate = pessoaFuncaoRepository.findAll().collectList().block().size();
        pessoaFuncao.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFuncao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaFuncao in the database
        List<PessoaFuncao> pessoaFuncaoList = pessoaFuncaoRepository.findAll().collectList().block();
        assertThat(pessoaFuncaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPessoaFuncao() throws Exception {
        int databaseSizeBeforeUpdate = pessoaFuncaoRepository.findAll().collectList().block().size();
        pessoaFuncao.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFuncao))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaFuncao in the database
        List<PessoaFuncao> pessoaFuncaoList = pessoaFuncaoRepository.findAll().collectList().block();
        assertThat(pessoaFuncaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePessoaFuncaoWithPatch() throws Exception {
        // Initialize the database
        pessoaFuncaoRepository.save(pessoaFuncao).block();

        int databaseSizeBeforeUpdate = pessoaFuncaoRepository.findAll().collectList().block().size();

        // Update the pessoaFuncao using partial update
        PessoaFuncao partialUpdatedPessoaFuncao = new PessoaFuncao();
        partialUpdatedPessoaFuncao.setId(pessoaFuncao.getId());

        partialUpdatedPessoaFuncao.ativo(UPDATED_ATIVO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaFuncao.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaFuncao))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaFuncao in the database
        List<PessoaFuncao> pessoaFuncaoList = pessoaFuncaoRepository.findAll().collectList().block();
        assertThat(pessoaFuncaoList).hasSize(databaseSizeBeforeUpdate);
        PessoaFuncao testPessoaFuncao = pessoaFuncaoList.get(pessoaFuncaoList.size() - 1);
        assertThat(testPessoaFuncao.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testPessoaFuncao.getAtivo()).isEqualTo(UPDATED_ATIVO);
    }

    @Test
    void fullUpdatePessoaFuncaoWithPatch() throws Exception {
        // Initialize the database
        pessoaFuncaoRepository.save(pessoaFuncao).block();

        int databaseSizeBeforeUpdate = pessoaFuncaoRepository.findAll().collectList().block().size();

        // Update the pessoaFuncao using partial update
        PessoaFuncao partialUpdatedPessoaFuncao = new PessoaFuncao();
        partialUpdatedPessoaFuncao.setId(pessoaFuncao.getId());

        partialUpdatedPessoaFuncao.descricao(UPDATED_DESCRICAO).ativo(UPDATED_ATIVO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaFuncao.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaFuncao))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaFuncao in the database
        List<PessoaFuncao> pessoaFuncaoList = pessoaFuncaoRepository.findAll().collectList().block();
        assertThat(pessoaFuncaoList).hasSize(databaseSizeBeforeUpdate);
        PessoaFuncao testPessoaFuncao = pessoaFuncaoList.get(pessoaFuncaoList.size() - 1);
        assertThat(testPessoaFuncao.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testPessoaFuncao.getAtivo()).isEqualTo(UPDATED_ATIVO);
    }

    @Test
    void patchNonExistingPessoaFuncao() throws Exception {
        int databaseSizeBeforeUpdate = pessoaFuncaoRepository.findAll().collectList().block().size();
        pessoaFuncao.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pessoaFuncao.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFuncao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaFuncao in the database
        List<PessoaFuncao> pessoaFuncaoList = pessoaFuncaoRepository.findAll().collectList().block();
        assertThat(pessoaFuncaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPessoaFuncao() throws Exception {
        int databaseSizeBeforeUpdate = pessoaFuncaoRepository.findAll().collectList().block().size();
        pessoaFuncao.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFuncao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaFuncao in the database
        List<PessoaFuncao> pessoaFuncaoList = pessoaFuncaoRepository.findAll().collectList().block();
        assertThat(pessoaFuncaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPessoaFuncao() throws Exception {
        int databaseSizeBeforeUpdate = pessoaFuncaoRepository.findAll().collectList().block().size();
        pessoaFuncao.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFuncao))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaFuncao in the database
        List<PessoaFuncao> pessoaFuncaoList = pessoaFuncaoRepository.findAll().collectList().block();
        assertThat(pessoaFuncaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePessoaFuncao() {
        // Initialize the database
        pessoaFuncaoRepository.save(pessoaFuncao).block();

        int databaseSizeBeforeDelete = pessoaFuncaoRepository.findAll().collectList().block().size();

        // Delete the pessoaFuncao
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pessoaFuncao.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<PessoaFuncao> pessoaFuncaoList = pessoaFuncaoRepository.findAll().collectList().block();
        assertThat(pessoaFuncaoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
