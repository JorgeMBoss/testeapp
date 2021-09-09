package com.mboss.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mboss.core.IntegrationTest;
import com.mboss.core.domain.PessoaEndereco;
import com.mboss.core.repository.PessoaEnderecoRepository;
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
 * Integration tests for the {@link PessoaEnderecoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PessoaEnderecoResourceIT {

    private static final String DEFAULT_PAIS = "AAAAAAAAAA";
    private static final String UPDATED_PAIS = "BBBBBBBBBB";

    private static final String DEFAULT_ESTADO = "AAAAAAAAAA";
    private static final String UPDATED_ESTADO = "BBBBBBBBBB";

    private static final String DEFAULT_CIDADE = "AAAAAAAAAA";
    private static final String UPDATED_CIDADE = "BBBBBBBBBB";

    private static final String DEFAULT_BAIRRO = "AAAAAAAAAA";
    private static final String UPDATED_BAIRRO = "BBBBBBBBBB";

    private static final Integer DEFAULT_NUMERO_RESIDENCIA = 1;
    private static final Integer UPDATED_NUMERO_RESIDENCIA = 2;

    private static final String DEFAULT_LOGRADOURO = "AAAAAAAAAA";
    private static final String UPDATED_LOGRADOURO = "BBBBBBBBBB";

    private static final String DEFAULT_COMPLEMENTO = "AAAAAAAAAA";
    private static final String UPDATED_COMPLEMENTO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/pessoa-enderecos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PessoaEnderecoRepository pessoaEnderecoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PessoaEndereco pessoaEndereco;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaEndereco createEntity(EntityManager em) {
        PessoaEndereco pessoaEndereco = new PessoaEndereco()
            .pais(DEFAULT_PAIS)
            .estado(DEFAULT_ESTADO)
            .cidade(DEFAULT_CIDADE)
            .bairro(DEFAULT_BAIRRO)
            .numeroResidencia(DEFAULT_NUMERO_RESIDENCIA)
            .logradouro(DEFAULT_LOGRADOURO)
            .complemento(DEFAULT_COMPLEMENTO);
        return pessoaEndereco;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaEndereco createUpdatedEntity(EntityManager em) {
        PessoaEndereco pessoaEndereco = new PessoaEndereco()
            .pais(UPDATED_PAIS)
            .estado(UPDATED_ESTADO)
            .cidade(UPDATED_CIDADE)
            .bairro(UPDATED_BAIRRO)
            .numeroResidencia(UPDATED_NUMERO_RESIDENCIA)
            .logradouro(UPDATED_LOGRADOURO)
            .complemento(UPDATED_COMPLEMENTO);
        return pessoaEndereco;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PessoaEndereco.class).block();
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
        pessoaEndereco = createEntity(em);
    }

    @Test
    void createPessoaEndereco() throws Exception {
        int databaseSizeBeforeCreate = pessoaEnderecoRepository.findAll().collectList().block().size();
        // Create the PessoaEndereco
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaEndereco))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the PessoaEndereco in the database
        List<PessoaEndereco> pessoaEnderecoList = pessoaEnderecoRepository.findAll().collectList().block();
        assertThat(pessoaEnderecoList).hasSize(databaseSizeBeforeCreate + 1);
        PessoaEndereco testPessoaEndereco = pessoaEnderecoList.get(pessoaEnderecoList.size() - 1);
        assertThat(testPessoaEndereco.getPais()).isEqualTo(DEFAULT_PAIS);
        assertThat(testPessoaEndereco.getEstado()).isEqualTo(DEFAULT_ESTADO);
        assertThat(testPessoaEndereco.getCidade()).isEqualTo(DEFAULT_CIDADE);
        assertThat(testPessoaEndereco.getBairro()).isEqualTo(DEFAULT_BAIRRO);
        assertThat(testPessoaEndereco.getNumeroResidencia()).isEqualTo(DEFAULT_NUMERO_RESIDENCIA);
        assertThat(testPessoaEndereco.getLogradouro()).isEqualTo(DEFAULT_LOGRADOURO);
        assertThat(testPessoaEndereco.getComplemento()).isEqualTo(DEFAULT_COMPLEMENTO);
    }

    @Test
    void createPessoaEnderecoWithExistingId() throws Exception {
        // Create the PessoaEndereco with an existing ID
        pessoaEndereco.setId(1L);

        int databaseSizeBeforeCreate = pessoaEnderecoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaEndereco))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaEndereco in the database
        List<PessoaEndereco> pessoaEnderecoList = pessoaEnderecoRepository.findAll().collectList().block();
        assertThat(pessoaEnderecoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPessoaEnderecosAsStream() {
        // Initialize the database
        pessoaEnderecoRepository.save(pessoaEndereco).block();

        List<PessoaEndereco> pessoaEnderecoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PessoaEndereco.class)
            .getResponseBody()
            .filter(pessoaEndereco::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(pessoaEnderecoList).isNotNull();
        assertThat(pessoaEnderecoList).hasSize(1);
        PessoaEndereco testPessoaEndereco = pessoaEnderecoList.get(0);
        assertThat(testPessoaEndereco.getPais()).isEqualTo(DEFAULT_PAIS);
        assertThat(testPessoaEndereco.getEstado()).isEqualTo(DEFAULT_ESTADO);
        assertThat(testPessoaEndereco.getCidade()).isEqualTo(DEFAULT_CIDADE);
        assertThat(testPessoaEndereco.getBairro()).isEqualTo(DEFAULT_BAIRRO);
        assertThat(testPessoaEndereco.getNumeroResidencia()).isEqualTo(DEFAULT_NUMERO_RESIDENCIA);
        assertThat(testPessoaEndereco.getLogradouro()).isEqualTo(DEFAULT_LOGRADOURO);
        assertThat(testPessoaEndereco.getComplemento()).isEqualTo(DEFAULT_COMPLEMENTO);
    }

    @Test
    void getAllPessoaEnderecos() {
        // Initialize the database
        pessoaEnderecoRepository.save(pessoaEndereco).block();

        // Get all the pessoaEnderecoList
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
            .value(hasItem(pessoaEndereco.getId().intValue()))
            .jsonPath("$.[*].pais")
            .value(hasItem(DEFAULT_PAIS))
            .jsonPath("$.[*].estado")
            .value(hasItem(DEFAULT_ESTADO))
            .jsonPath("$.[*].cidade")
            .value(hasItem(DEFAULT_CIDADE))
            .jsonPath("$.[*].bairro")
            .value(hasItem(DEFAULT_BAIRRO))
            .jsonPath("$.[*].numeroResidencia")
            .value(hasItem(DEFAULT_NUMERO_RESIDENCIA))
            .jsonPath("$.[*].logradouro")
            .value(hasItem(DEFAULT_LOGRADOURO))
            .jsonPath("$.[*].complemento")
            .value(hasItem(DEFAULT_COMPLEMENTO));
    }

    @Test
    void getPessoaEndereco() {
        // Initialize the database
        pessoaEnderecoRepository.save(pessoaEndereco).block();

        // Get the pessoaEndereco
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pessoaEndereco.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pessoaEndereco.getId().intValue()))
            .jsonPath("$.pais")
            .value(is(DEFAULT_PAIS))
            .jsonPath("$.estado")
            .value(is(DEFAULT_ESTADO))
            .jsonPath("$.cidade")
            .value(is(DEFAULT_CIDADE))
            .jsonPath("$.bairro")
            .value(is(DEFAULT_BAIRRO))
            .jsonPath("$.numeroResidencia")
            .value(is(DEFAULT_NUMERO_RESIDENCIA))
            .jsonPath("$.logradouro")
            .value(is(DEFAULT_LOGRADOURO))
            .jsonPath("$.complemento")
            .value(is(DEFAULT_COMPLEMENTO));
    }

    @Test
    void getNonExistingPessoaEndereco() {
        // Get the pessoaEndereco
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPessoaEndereco() throws Exception {
        // Initialize the database
        pessoaEnderecoRepository.save(pessoaEndereco).block();

        int databaseSizeBeforeUpdate = pessoaEnderecoRepository.findAll().collectList().block().size();

        // Update the pessoaEndereco
        PessoaEndereco updatedPessoaEndereco = pessoaEnderecoRepository.findById(pessoaEndereco.getId()).block();
        updatedPessoaEndereco
            .pais(UPDATED_PAIS)
            .estado(UPDATED_ESTADO)
            .cidade(UPDATED_CIDADE)
            .bairro(UPDATED_BAIRRO)
            .numeroResidencia(UPDATED_NUMERO_RESIDENCIA)
            .logradouro(UPDATED_LOGRADOURO)
            .complemento(UPDATED_COMPLEMENTO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPessoaEndereco.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPessoaEndereco))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaEndereco in the database
        List<PessoaEndereco> pessoaEnderecoList = pessoaEnderecoRepository.findAll().collectList().block();
        assertThat(pessoaEnderecoList).hasSize(databaseSizeBeforeUpdate);
        PessoaEndereco testPessoaEndereco = pessoaEnderecoList.get(pessoaEnderecoList.size() - 1);
        assertThat(testPessoaEndereco.getPais()).isEqualTo(UPDATED_PAIS);
        assertThat(testPessoaEndereco.getEstado()).isEqualTo(UPDATED_ESTADO);
        assertThat(testPessoaEndereco.getCidade()).isEqualTo(UPDATED_CIDADE);
        assertThat(testPessoaEndereco.getBairro()).isEqualTo(UPDATED_BAIRRO);
        assertThat(testPessoaEndereco.getNumeroResidencia()).isEqualTo(UPDATED_NUMERO_RESIDENCIA);
        assertThat(testPessoaEndereco.getLogradouro()).isEqualTo(UPDATED_LOGRADOURO);
        assertThat(testPessoaEndereco.getComplemento()).isEqualTo(UPDATED_COMPLEMENTO);
    }

    @Test
    void putNonExistingPessoaEndereco() throws Exception {
        int databaseSizeBeforeUpdate = pessoaEnderecoRepository.findAll().collectList().block().size();
        pessoaEndereco.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pessoaEndereco.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaEndereco))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaEndereco in the database
        List<PessoaEndereco> pessoaEnderecoList = pessoaEnderecoRepository.findAll().collectList().block();
        assertThat(pessoaEnderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPessoaEndereco() throws Exception {
        int databaseSizeBeforeUpdate = pessoaEnderecoRepository.findAll().collectList().block().size();
        pessoaEndereco.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaEndereco))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaEndereco in the database
        List<PessoaEndereco> pessoaEnderecoList = pessoaEnderecoRepository.findAll().collectList().block();
        assertThat(pessoaEnderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPessoaEndereco() throws Exception {
        int databaseSizeBeforeUpdate = pessoaEnderecoRepository.findAll().collectList().block().size();
        pessoaEndereco.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaEndereco))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaEndereco in the database
        List<PessoaEndereco> pessoaEnderecoList = pessoaEnderecoRepository.findAll().collectList().block();
        assertThat(pessoaEnderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePessoaEnderecoWithPatch() throws Exception {
        // Initialize the database
        pessoaEnderecoRepository.save(pessoaEndereco).block();

        int databaseSizeBeforeUpdate = pessoaEnderecoRepository.findAll().collectList().block().size();

        // Update the pessoaEndereco using partial update
        PessoaEndereco partialUpdatedPessoaEndereco = new PessoaEndereco();
        partialUpdatedPessoaEndereco.setId(pessoaEndereco.getId());

        partialUpdatedPessoaEndereco
            .cidade(UPDATED_CIDADE)
            .bairro(UPDATED_BAIRRO)
            .numeroResidencia(UPDATED_NUMERO_RESIDENCIA)
            .logradouro(UPDATED_LOGRADOURO)
            .complemento(UPDATED_COMPLEMENTO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaEndereco.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaEndereco))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaEndereco in the database
        List<PessoaEndereco> pessoaEnderecoList = pessoaEnderecoRepository.findAll().collectList().block();
        assertThat(pessoaEnderecoList).hasSize(databaseSizeBeforeUpdate);
        PessoaEndereco testPessoaEndereco = pessoaEnderecoList.get(pessoaEnderecoList.size() - 1);
        assertThat(testPessoaEndereco.getPais()).isEqualTo(DEFAULT_PAIS);
        assertThat(testPessoaEndereco.getEstado()).isEqualTo(DEFAULT_ESTADO);
        assertThat(testPessoaEndereco.getCidade()).isEqualTo(UPDATED_CIDADE);
        assertThat(testPessoaEndereco.getBairro()).isEqualTo(UPDATED_BAIRRO);
        assertThat(testPessoaEndereco.getNumeroResidencia()).isEqualTo(UPDATED_NUMERO_RESIDENCIA);
        assertThat(testPessoaEndereco.getLogradouro()).isEqualTo(UPDATED_LOGRADOURO);
        assertThat(testPessoaEndereco.getComplemento()).isEqualTo(UPDATED_COMPLEMENTO);
    }

    @Test
    void fullUpdatePessoaEnderecoWithPatch() throws Exception {
        // Initialize the database
        pessoaEnderecoRepository.save(pessoaEndereco).block();

        int databaseSizeBeforeUpdate = pessoaEnderecoRepository.findAll().collectList().block().size();

        // Update the pessoaEndereco using partial update
        PessoaEndereco partialUpdatedPessoaEndereco = new PessoaEndereco();
        partialUpdatedPessoaEndereco.setId(pessoaEndereco.getId());

        partialUpdatedPessoaEndereco
            .pais(UPDATED_PAIS)
            .estado(UPDATED_ESTADO)
            .cidade(UPDATED_CIDADE)
            .bairro(UPDATED_BAIRRO)
            .numeroResidencia(UPDATED_NUMERO_RESIDENCIA)
            .logradouro(UPDATED_LOGRADOURO)
            .complemento(UPDATED_COMPLEMENTO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaEndereco.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaEndereco))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaEndereco in the database
        List<PessoaEndereco> pessoaEnderecoList = pessoaEnderecoRepository.findAll().collectList().block();
        assertThat(pessoaEnderecoList).hasSize(databaseSizeBeforeUpdate);
        PessoaEndereco testPessoaEndereco = pessoaEnderecoList.get(pessoaEnderecoList.size() - 1);
        assertThat(testPessoaEndereco.getPais()).isEqualTo(UPDATED_PAIS);
        assertThat(testPessoaEndereco.getEstado()).isEqualTo(UPDATED_ESTADO);
        assertThat(testPessoaEndereco.getCidade()).isEqualTo(UPDATED_CIDADE);
        assertThat(testPessoaEndereco.getBairro()).isEqualTo(UPDATED_BAIRRO);
        assertThat(testPessoaEndereco.getNumeroResidencia()).isEqualTo(UPDATED_NUMERO_RESIDENCIA);
        assertThat(testPessoaEndereco.getLogradouro()).isEqualTo(UPDATED_LOGRADOURO);
        assertThat(testPessoaEndereco.getComplemento()).isEqualTo(UPDATED_COMPLEMENTO);
    }

    @Test
    void patchNonExistingPessoaEndereco() throws Exception {
        int databaseSizeBeforeUpdate = pessoaEnderecoRepository.findAll().collectList().block().size();
        pessoaEndereco.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pessoaEndereco.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaEndereco))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaEndereco in the database
        List<PessoaEndereco> pessoaEnderecoList = pessoaEnderecoRepository.findAll().collectList().block();
        assertThat(pessoaEnderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPessoaEndereco() throws Exception {
        int databaseSizeBeforeUpdate = pessoaEnderecoRepository.findAll().collectList().block().size();
        pessoaEndereco.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaEndereco))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaEndereco in the database
        List<PessoaEndereco> pessoaEnderecoList = pessoaEnderecoRepository.findAll().collectList().block();
        assertThat(pessoaEnderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPessoaEndereco() throws Exception {
        int databaseSizeBeforeUpdate = pessoaEnderecoRepository.findAll().collectList().block().size();
        pessoaEndereco.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaEndereco))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaEndereco in the database
        List<PessoaEndereco> pessoaEnderecoList = pessoaEnderecoRepository.findAll().collectList().block();
        assertThat(pessoaEnderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePessoaEndereco() {
        // Initialize the database
        pessoaEnderecoRepository.save(pessoaEndereco).block();

        int databaseSizeBeforeDelete = pessoaEnderecoRepository.findAll().collectList().block().size();

        // Delete the pessoaEndereco
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pessoaEndereco.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<PessoaEndereco> pessoaEnderecoList = pessoaEnderecoRepository.findAll().collectList().block();
        assertThat(pessoaEnderecoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
