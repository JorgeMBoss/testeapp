package com.mboss.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mboss.core.IntegrationTest;
import com.mboss.core.domain.PessoaFisca;
import com.mboss.core.repository.PessoaFiscaRepository;
import com.mboss.core.service.EntityManager;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link PessoaFiscaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PessoaFiscaResourceIT {

    private static final String DEFAULT_CPF = "AAAAAAAAAAA";
    private static final String UPDATED_CPF = "BBBBBBBBBBB";

    private static final String DEFAULT_RG = "AAAAAAAAAA";
    private static final String UPDATED_RG = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATA_NASCIMENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_NASCIMENTO = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_IDADE = 1;
    private static final Integer UPDATED_IDADE = 2;

    private static final String DEFAULT_SEXO = "AAAAAAAAAA";
    private static final String UPDATED_SEXO = "BBBBBBBBBB";

    private static final String DEFAULT_COR = "AAAAAAAAAA";
    private static final String UPDATED_COR = "BBBBBBBBBB";

    private static final String DEFAULT_ESTADO_CIVIL = "AAAAAAAAAA";
    private static final String UPDATED_ESTADO_CIVIL = "BBBBBBBBBB";

    private static final String DEFAULT_NATURALIDADE = "AAAAAAAAAA";
    private static final String UPDATED_NATURALIDADE = "BBBBBBBBBB";

    private static final String DEFAULT_NACIONALIDADE = "AAAAAAAAAA";
    private static final String UPDATED_NACIONALIDADE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/pessoa-fiscas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PessoaFiscaRepository pessoaFiscaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PessoaFisca pessoaFisca;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaFisca createEntity(EntityManager em) {
        PessoaFisca pessoaFisca = new PessoaFisca()
            .cpf(DEFAULT_CPF)
            .rg(DEFAULT_RG)
            .dataNascimento(DEFAULT_DATA_NASCIMENTO)
            .idade(DEFAULT_IDADE)
            .sexo(DEFAULT_SEXO)
            .cor(DEFAULT_COR)
            .estadoCivil(DEFAULT_ESTADO_CIVIL)
            .naturalidade(DEFAULT_NATURALIDADE)
            .nacionalidade(DEFAULT_NACIONALIDADE);
        return pessoaFisca;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaFisca createUpdatedEntity(EntityManager em) {
        PessoaFisca pessoaFisca = new PessoaFisca()
            .cpf(UPDATED_CPF)
            .rg(UPDATED_RG)
            .dataNascimento(UPDATED_DATA_NASCIMENTO)
            .idade(UPDATED_IDADE)
            .sexo(UPDATED_SEXO)
            .cor(UPDATED_COR)
            .estadoCivil(UPDATED_ESTADO_CIVIL)
            .naturalidade(UPDATED_NATURALIDADE)
            .nacionalidade(UPDATED_NACIONALIDADE);
        return pessoaFisca;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PessoaFisca.class).block();
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
        pessoaFisca = createEntity(em);
    }

    @Test
    void createPessoaFisca() throws Exception {
        int databaseSizeBeforeCreate = pessoaFiscaRepository.findAll().collectList().block().size();
        // Create the PessoaFisca
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFisca))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the PessoaFisca in the database
        List<PessoaFisca> pessoaFiscaList = pessoaFiscaRepository.findAll().collectList().block();
        assertThat(pessoaFiscaList).hasSize(databaseSizeBeforeCreate + 1);
        PessoaFisca testPessoaFisca = pessoaFiscaList.get(pessoaFiscaList.size() - 1);
        assertThat(testPessoaFisca.getCpf()).isEqualTo(DEFAULT_CPF);
        assertThat(testPessoaFisca.getRg()).isEqualTo(DEFAULT_RG);
        assertThat(testPessoaFisca.getDataNascimento()).isEqualTo(DEFAULT_DATA_NASCIMENTO);
        assertThat(testPessoaFisca.getIdade()).isEqualTo(DEFAULT_IDADE);
        assertThat(testPessoaFisca.getSexo()).isEqualTo(DEFAULT_SEXO);
        assertThat(testPessoaFisca.getCor()).isEqualTo(DEFAULT_COR);
        assertThat(testPessoaFisca.getEstadoCivil()).isEqualTo(DEFAULT_ESTADO_CIVIL);
        assertThat(testPessoaFisca.getNaturalidade()).isEqualTo(DEFAULT_NATURALIDADE);
        assertThat(testPessoaFisca.getNacionalidade()).isEqualTo(DEFAULT_NACIONALIDADE);
    }

    @Test
    void createPessoaFiscaWithExistingId() throws Exception {
        // Create the PessoaFisca with an existing ID
        pessoaFisca.setId(1L);

        int databaseSizeBeforeCreate = pessoaFiscaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFisca))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaFisca in the database
        List<PessoaFisca> pessoaFiscaList = pessoaFiscaRepository.findAll().collectList().block();
        assertThat(pessoaFiscaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPessoaFiscasAsStream() {
        // Initialize the database
        pessoaFiscaRepository.save(pessoaFisca).block();

        List<PessoaFisca> pessoaFiscaList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PessoaFisca.class)
            .getResponseBody()
            .filter(pessoaFisca::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(pessoaFiscaList).isNotNull();
        assertThat(pessoaFiscaList).hasSize(1);
        PessoaFisca testPessoaFisca = pessoaFiscaList.get(0);
        assertThat(testPessoaFisca.getCpf()).isEqualTo(DEFAULT_CPF);
        assertThat(testPessoaFisca.getRg()).isEqualTo(DEFAULT_RG);
        assertThat(testPessoaFisca.getDataNascimento()).isEqualTo(DEFAULT_DATA_NASCIMENTO);
        assertThat(testPessoaFisca.getIdade()).isEqualTo(DEFAULT_IDADE);
        assertThat(testPessoaFisca.getSexo()).isEqualTo(DEFAULT_SEXO);
        assertThat(testPessoaFisca.getCor()).isEqualTo(DEFAULT_COR);
        assertThat(testPessoaFisca.getEstadoCivil()).isEqualTo(DEFAULT_ESTADO_CIVIL);
        assertThat(testPessoaFisca.getNaturalidade()).isEqualTo(DEFAULT_NATURALIDADE);
        assertThat(testPessoaFisca.getNacionalidade()).isEqualTo(DEFAULT_NACIONALIDADE);
    }

    @Test
    void getAllPessoaFiscas() {
        // Initialize the database
        pessoaFiscaRepository.save(pessoaFisca).block();

        // Get all the pessoaFiscaList
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
            .value(hasItem(pessoaFisca.getId().intValue()))
            .jsonPath("$.[*].cpf")
            .value(hasItem(DEFAULT_CPF))
            .jsonPath("$.[*].rg")
            .value(hasItem(DEFAULT_RG))
            .jsonPath("$.[*].dataNascimento")
            .value(hasItem(DEFAULT_DATA_NASCIMENTO.toString()))
            .jsonPath("$.[*].idade")
            .value(hasItem(DEFAULT_IDADE))
            .jsonPath("$.[*].sexo")
            .value(hasItem(DEFAULT_SEXO))
            .jsonPath("$.[*].cor")
            .value(hasItem(DEFAULT_COR))
            .jsonPath("$.[*].estadoCivil")
            .value(hasItem(DEFAULT_ESTADO_CIVIL))
            .jsonPath("$.[*].naturalidade")
            .value(hasItem(DEFAULT_NATURALIDADE))
            .jsonPath("$.[*].nacionalidade")
            .value(hasItem(DEFAULT_NACIONALIDADE));
    }

    @Test
    void getPessoaFisca() {
        // Initialize the database
        pessoaFiscaRepository.save(pessoaFisca).block();

        // Get the pessoaFisca
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pessoaFisca.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pessoaFisca.getId().intValue()))
            .jsonPath("$.cpf")
            .value(is(DEFAULT_CPF))
            .jsonPath("$.rg")
            .value(is(DEFAULT_RG))
            .jsonPath("$.dataNascimento")
            .value(is(DEFAULT_DATA_NASCIMENTO.toString()))
            .jsonPath("$.idade")
            .value(is(DEFAULT_IDADE))
            .jsonPath("$.sexo")
            .value(is(DEFAULT_SEXO))
            .jsonPath("$.cor")
            .value(is(DEFAULT_COR))
            .jsonPath("$.estadoCivil")
            .value(is(DEFAULT_ESTADO_CIVIL))
            .jsonPath("$.naturalidade")
            .value(is(DEFAULT_NATURALIDADE))
            .jsonPath("$.nacionalidade")
            .value(is(DEFAULT_NACIONALIDADE));
    }

    @Test
    void getNonExistingPessoaFisca() {
        // Get the pessoaFisca
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPessoaFisca() throws Exception {
        // Initialize the database
        pessoaFiscaRepository.save(pessoaFisca).block();

        int databaseSizeBeforeUpdate = pessoaFiscaRepository.findAll().collectList().block().size();

        // Update the pessoaFisca
        PessoaFisca updatedPessoaFisca = pessoaFiscaRepository.findById(pessoaFisca.getId()).block();
        updatedPessoaFisca
            .cpf(UPDATED_CPF)
            .rg(UPDATED_RG)
            .dataNascimento(UPDATED_DATA_NASCIMENTO)
            .idade(UPDATED_IDADE)
            .sexo(UPDATED_SEXO)
            .cor(UPDATED_COR)
            .estadoCivil(UPDATED_ESTADO_CIVIL)
            .naturalidade(UPDATED_NATURALIDADE)
            .nacionalidade(UPDATED_NACIONALIDADE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPessoaFisca.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPessoaFisca))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaFisca in the database
        List<PessoaFisca> pessoaFiscaList = pessoaFiscaRepository.findAll().collectList().block();
        assertThat(pessoaFiscaList).hasSize(databaseSizeBeforeUpdate);
        PessoaFisca testPessoaFisca = pessoaFiscaList.get(pessoaFiscaList.size() - 1);
        assertThat(testPessoaFisca.getCpf()).isEqualTo(UPDATED_CPF);
        assertThat(testPessoaFisca.getRg()).isEqualTo(UPDATED_RG);
        assertThat(testPessoaFisca.getDataNascimento()).isEqualTo(UPDATED_DATA_NASCIMENTO);
        assertThat(testPessoaFisca.getIdade()).isEqualTo(UPDATED_IDADE);
        assertThat(testPessoaFisca.getSexo()).isEqualTo(UPDATED_SEXO);
        assertThat(testPessoaFisca.getCor()).isEqualTo(UPDATED_COR);
        assertThat(testPessoaFisca.getEstadoCivil()).isEqualTo(UPDATED_ESTADO_CIVIL);
        assertThat(testPessoaFisca.getNaturalidade()).isEqualTo(UPDATED_NATURALIDADE);
        assertThat(testPessoaFisca.getNacionalidade()).isEqualTo(UPDATED_NACIONALIDADE);
    }

    @Test
    void putNonExistingPessoaFisca() throws Exception {
        int databaseSizeBeforeUpdate = pessoaFiscaRepository.findAll().collectList().block().size();
        pessoaFisca.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pessoaFisca.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFisca))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaFisca in the database
        List<PessoaFisca> pessoaFiscaList = pessoaFiscaRepository.findAll().collectList().block();
        assertThat(pessoaFiscaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPessoaFisca() throws Exception {
        int databaseSizeBeforeUpdate = pessoaFiscaRepository.findAll().collectList().block().size();
        pessoaFisca.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFisca))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaFisca in the database
        List<PessoaFisca> pessoaFiscaList = pessoaFiscaRepository.findAll().collectList().block();
        assertThat(pessoaFiscaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPessoaFisca() throws Exception {
        int databaseSizeBeforeUpdate = pessoaFiscaRepository.findAll().collectList().block().size();
        pessoaFisca.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFisca))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaFisca in the database
        List<PessoaFisca> pessoaFiscaList = pessoaFiscaRepository.findAll().collectList().block();
        assertThat(pessoaFiscaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePessoaFiscaWithPatch() throws Exception {
        // Initialize the database
        pessoaFiscaRepository.save(pessoaFisca).block();

        int databaseSizeBeforeUpdate = pessoaFiscaRepository.findAll().collectList().block().size();

        // Update the pessoaFisca using partial update
        PessoaFisca partialUpdatedPessoaFisca = new PessoaFisca();
        partialUpdatedPessoaFisca.setId(pessoaFisca.getId());

        partialUpdatedPessoaFisca.cpf(UPDATED_CPF).rg(UPDATED_RG).sexo(UPDATED_SEXO).cor(UPDATED_COR);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaFisca.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaFisca))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaFisca in the database
        List<PessoaFisca> pessoaFiscaList = pessoaFiscaRepository.findAll().collectList().block();
        assertThat(pessoaFiscaList).hasSize(databaseSizeBeforeUpdate);
        PessoaFisca testPessoaFisca = pessoaFiscaList.get(pessoaFiscaList.size() - 1);
        assertThat(testPessoaFisca.getCpf()).isEqualTo(UPDATED_CPF);
        assertThat(testPessoaFisca.getRg()).isEqualTo(UPDATED_RG);
        assertThat(testPessoaFisca.getDataNascimento()).isEqualTo(DEFAULT_DATA_NASCIMENTO);
        assertThat(testPessoaFisca.getIdade()).isEqualTo(DEFAULT_IDADE);
        assertThat(testPessoaFisca.getSexo()).isEqualTo(UPDATED_SEXO);
        assertThat(testPessoaFisca.getCor()).isEqualTo(UPDATED_COR);
        assertThat(testPessoaFisca.getEstadoCivil()).isEqualTo(DEFAULT_ESTADO_CIVIL);
        assertThat(testPessoaFisca.getNaturalidade()).isEqualTo(DEFAULT_NATURALIDADE);
        assertThat(testPessoaFisca.getNacionalidade()).isEqualTo(DEFAULT_NACIONALIDADE);
    }

    @Test
    void fullUpdatePessoaFiscaWithPatch() throws Exception {
        // Initialize the database
        pessoaFiscaRepository.save(pessoaFisca).block();

        int databaseSizeBeforeUpdate = pessoaFiscaRepository.findAll().collectList().block().size();

        // Update the pessoaFisca using partial update
        PessoaFisca partialUpdatedPessoaFisca = new PessoaFisca();
        partialUpdatedPessoaFisca.setId(pessoaFisca.getId());

        partialUpdatedPessoaFisca
            .cpf(UPDATED_CPF)
            .rg(UPDATED_RG)
            .dataNascimento(UPDATED_DATA_NASCIMENTO)
            .idade(UPDATED_IDADE)
            .sexo(UPDATED_SEXO)
            .cor(UPDATED_COR)
            .estadoCivil(UPDATED_ESTADO_CIVIL)
            .naturalidade(UPDATED_NATURALIDADE)
            .nacionalidade(UPDATED_NACIONALIDADE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaFisca.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaFisca))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaFisca in the database
        List<PessoaFisca> pessoaFiscaList = pessoaFiscaRepository.findAll().collectList().block();
        assertThat(pessoaFiscaList).hasSize(databaseSizeBeforeUpdate);
        PessoaFisca testPessoaFisca = pessoaFiscaList.get(pessoaFiscaList.size() - 1);
        assertThat(testPessoaFisca.getCpf()).isEqualTo(UPDATED_CPF);
        assertThat(testPessoaFisca.getRg()).isEqualTo(UPDATED_RG);
        assertThat(testPessoaFisca.getDataNascimento()).isEqualTo(UPDATED_DATA_NASCIMENTO);
        assertThat(testPessoaFisca.getIdade()).isEqualTo(UPDATED_IDADE);
        assertThat(testPessoaFisca.getSexo()).isEqualTo(UPDATED_SEXO);
        assertThat(testPessoaFisca.getCor()).isEqualTo(UPDATED_COR);
        assertThat(testPessoaFisca.getEstadoCivil()).isEqualTo(UPDATED_ESTADO_CIVIL);
        assertThat(testPessoaFisca.getNaturalidade()).isEqualTo(UPDATED_NATURALIDADE);
        assertThat(testPessoaFisca.getNacionalidade()).isEqualTo(UPDATED_NACIONALIDADE);
    }

    @Test
    void patchNonExistingPessoaFisca() throws Exception {
        int databaseSizeBeforeUpdate = pessoaFiscaRepository.findAll().collectList().block().size();
        pessoaFisca.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pessoaFisca.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFisca))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaFisca in the database
        List<PessoaFisca> pessoaFiscaList = pessoaFiscaRepository.findAll().collectList().block();
        assertThat(pessoaFiscaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPessoaFisca() throws Exception {
        int databaseSizeBeforeUpdate = pessoaFiscaRepository.findAll().collectList().block().size();
        pessoaFisca.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFisca))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaFisca in the database
        List<PessoaFisca> pessoaFiscaList = pessoaFiscaRepository.findAll().collectList().block();
        assertThat(pessoaFiscaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPessoaFisca() throws Exception {
        int databaseSizeBeforeUpdate = pessoaFiscaRepository.findAll().collectList().block().size();
        pessoaFisca.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFisca))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaFisca in the database
        List<PessoaFisca> pessoaFiscaList = pessoaFiscaRepository.findAll().collectList().block();
        assertThat(pessoaFiscaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePessoaFisca() {
        // Initialize the database
        pessoaFiscaRepository.save(pessoaFisca).block();

        int databaseSizeBeforeDelete = pessoaFiscaRepository.findAll().collectList().block().size();

        // Delete the pessoaFisca
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pessoaFisca.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<PessoaFisca> pessoaFiscaList = pessoaFiscaRepository.findAll().collectList().block();
        assertThat(pessoaFiscaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
