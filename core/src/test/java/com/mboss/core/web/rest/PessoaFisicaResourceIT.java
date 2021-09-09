package com.mboss.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mboss.core.IntegrationTest;
import com.mboss.core.domain.PessoaFisica;
import com.mboss.core.repository.PessoaFisicaRepository;
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
 * Integration tests for the {@link PessoaFisicaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PessoaFisicaResourceIT {

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

    private static final String ENTITY_API_URL = "/api/pessoa-fisicas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PessoaFisicaRepository pessoaFisicaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PessoaFisica pessoaFisica;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaFisica createEntity(EntityManager em) {
        PessoaFisica pessoaFisica = new PessoaFisica()
            .cpf(DEFAULT_CPF)
            .rg(DEFAULT_RG)
            .dataNascimento(DEFAULT_DATA_NASCIMENTO)
            .idade(DEFAULT_IDADE)
            .sexo(DEFAULT_SEXO)
            .cor(DEFAULT_COR)
            .estadoCivil(DEFAULT_ESTADO_CIVIL)
            .naturalidade(DEFAULT_NATURALIDADE)
            .nacionalidade(DEFAULT_NACIONALIDADE);
        return pessoaFisica;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaFisica createUpdatedEntity(EntityManager em) {
        PessoaFisica pessoaFisica = new PessoaFisica()
            .cpf(UPDATED_CPF)
            .rg(UPDATED_RG)
            .dataNascimento(UPDATED_DATA_NASCIMENTO)
            .idade(UPDATED_IDADE)
            .sexo(UPDATED_SEXO)
            .cor(UPDATED_COR)
            .estadoCivil(UPDATED_ESTADO_CIVIL)
            .naturalidade(UPDATED_NATURALIDADE)
            .nacionalidade(UPDATED_NACIONALIDADE);
        return pessoaFisica;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PessoaFisica.class).block();
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
        pessoaFisica = createEntity(em);
    }

    @Test
    void createPessoaFisica() throws Exception {
        int databaseSizeBeforeCreate = pessoaFisicaRepository.findAll().collectList().block().size();
        // Create the PessoaFisica
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFisica))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the PessoaFisica in the database
        List<PessoaFisica> pessoaFisicaList = pessoaFisicaRepository.findAll().collectList().block();
        assertThat(pessoaFisicaList).hasSize(databaseSizeBeforeCreate + 1);
        PessoaFisica testPessoaFisica = pessoaFisicaList.get(pessoaFisicaList.size() - 1);
        assertThat(testPessoaFisica.getCpf()).isEqualTo(DEFAULT_CPF);
        assertThat(testPessoaFisica.getRg()).isEqualTo(DEFAULT_RG);
        assertThat(testPessoaFisica.getDataNascimento()).isEqualTo(DEFAULT_DATA_NASCIMENTO);
        assertThat(testPessoaFisica.getIdade()).isEqualTo(DEFAULT_IDADE);
        assertThat(testPessoaFisica.getSexo()).isEqualTo(DEFAULT_SEXO);
        assertThat(testPessoaFisica.getCor()).isEqualTo(DEFAULT_COR);
        assertThat(testPessoaFisica.getEstadoCivil()).isEqualTo(DEFAULT_ESTADO_CIVIL);
        assertThat(testPessoaFisica.getNaturalidade()).isEqualTo(DEFAULT_NATURALIDADE);
        assertThat(testPessoaFisica.getNacionalidade()).isEqualTo(DEFAULT_NACIONALIDADE);
    }

    @Test
    void createPessoaFisicaWithExistingId() throws Exception {
        // Create the PessoaFisica with an existing ID
        pessoaFisica.setId(1L);

        int databaseSizeBeforeCreate = pessoaFisicaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFisica))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaFisica in the database
        List<PessoaFisica> pessoaFisicaList = pessoaFisicaRepository.findAll().collectList().block();
        assertThat(pessoaFisicaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPessoaFisicasAsStream() {
        // Initialize the database
        pessoaFisicaRepository.save(pessoaFisica).block();

        List<PessoaFisica> pessoaFisicaList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PessoaFisica.class)
            .getResponseBody()
            .filter(pessoaFisica::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(pessoaFisicaList).isNotNull();
        assertThat(pessoaFisicaList).hasSize(1);
        PessoaFisica testPessoaFisica = pessoaFisicaList.get(0);
        assertThat(testPessoaFisica.getCpf()).isEqualTo(DEFAULT_CPF);
        assertThat(testPessoaFisica.getRg()).isEqualTo(DEFAULT_RG);
        assertThat(testPessoaFisica.getDataNascimento()).isEqualTo(DEFAULT_DATA_NASCIMENTO);
        assertThat(testPessoaFisica.getIdade()).isEqualTo(DEFAULT_IDADE);
        assertThat(testPessoaFisica.getSexo()).isEqualTo(DEFAULT_SEXO);
        assertThat(testPessoaFisica.getCor()).isEqualTo(DEFAULT_COR);
        assertThat(testPessoaFisica.getEstadoCivil()).isEqualTo(DEFAULT_ESTADO_CIVIL);
        assertThat(testPessoaFisica.getNaturalidade()).isEqualTo(DEFAULT_NATURALIDADE);
        assertThat(testPessoaFisica.getNacionalidade()).isEqualTo(DEFAULT_NACIONALIDADE);
    }

    @Test
    void getAllPessoaFisicas() {
        // Initialize the database
        pessoaFisicaRepository.save(pessoaFisica).block();

        // Get all the pessoaFisicaList
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
            .value(hasItem(pessoaFisica.getId().intValue()))
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
    void getPessoaFisica() {
        // Initialize the database
        pessoaFisicaRepository.save(pessoaFisica).block();

        // Get the pessoaFisica
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pessoaFisica.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pessoaFisica.getId().intValue()))
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
    void getNonExistingPessoaFisica() {
        // Get the pessoaFisica
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPessoaFisica() throws Exception {
        // Initialize the database
        pessoaFisicaRepository.save(pessoaFisica).block();

        int databaseSizeBeforeUpdate = pessoaFisicaRepository.findAll().collectList().block().size();

        // Update the pessoaFisica
        PessoaFisica updatedPessoaFisica = pessoaFisicaRepository.findById(pessoaFisica.getId()).block();
        updatedPessoaFisica
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
            .uri(ENTITY_API_URL_ID, updatedPessoaFisica.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPessoaFisica))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaFisica in the database
        List<PessoaFisica> pessoaFisicaList = pessoaFisicaRepository.findAll().collectList().block();
        assertThat(pessoaFisicaList).hasSize(databaseSizeBeforeUpdate);
        PessoaFisica testPessoaFisica = pessoaFisicaList.get(pessoaFisicaList.size() - 1);
        assertThat(testPessoaFisica.getCpf()).isEqualTo(UPDATED_CPF);
        assertThat(testPessoaFisica.getRg()).isEqualTo(UPDATED_RG);
        assertThat(testPessoaFisica.getDataNascimento()).isEqualTo(UPDATED_DATA_NASCIMENTO);
        assertThat(testPessoaFisica.getIdade()).isEqualTo(UPDATED_IDADE);
        assertThat(testPessoaFisica.getSexo()).isEqualTo(UPDATED_SEXO);
        assertThat(testPessoaFisica.getCor()).isEqualTo(UPDATED_COR);
        assertThat(testPessoaFisica.getEstadoCivil()).isEqualTo(UPDATED_ESTADO_CIVIL);
        assertThat(testPessoaFisica.getNaturalidade()).isEqualTo(UPDATED_NATURALIDADE);
        assertThat(testPessoaFisica.getNacionalidade()).isEqualTo(UPDATED_NACIONALIDADE);
    }

    @Test
    void putNonExistingPessoaFisica() throws Exception {
        int databaseSizeBeforeUpdate = pessoaFisicaRepository.findAll().collectList().block().size();
        pessoaFisica.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pessoaFisica.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFisica))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaFisica in the database
        List<PessoaFisica> pessoaFisicaList = pessoaFisicaRepository.findAll().collectList().block();
        assertThat(pessoaFisicaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPessoaFisica() throws Exception {
        int databaseSizeBeforeUpdate = pessoaFisicaRepository.findAll().collectList().block().size();
        pessoaFisica.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFisica))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaFisica in the database
        List<PessoaFisica> pessoaFisicaList = pessoaFisicaRepository.findAll().collectList().block();
        assertThat(pessoaFisicaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPessoaFisica() throws Exception {
        int databaseSizeBeforeUpdate = pessoaFisicaRepository.findAll().collectList().block().size();
        pessoaFisica.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFisica))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaFisica in the database
        List<PessoaFisica> pessoaFisicaList = pessoaFisicaRepository.findAll().collectList().block();
        assertThat(pessoaFisicaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePessoaFisicaWithPatch() throws Exception {
        // Initialize the database
        pessoaFisicaRepository.save(pessoaFisica).block();

        int databaseSizeBeforeUpdate = pessoaFisicaRepository.findAll().collectList().block().size();

        // Update the pessoaFisica using partial update
        PessoaFisica partialUpdatedPessoaFisica = new PessoaFisica();
        partialUpdatedPessoaFisica.setId(pessoaFisica.getId());

        partialUpdatedPessoaFisica
            .cpf(UPDATED_CPF)
            .idade(UPDATED_IDADE)
            .sexo(UPDATED_SEXO)
            .cor(UPDATED_COR)
            .estadoCivil(UPDATED_ESTADO_CIVIL)
            .nacionalidade(UPDATED_NACIONALIDADE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaFisica.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaFisica))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaFisica in the database
        List<PessoaFisica> pessoaFisicaList = pessoaFisicaRepository.findAll().collectList().block();
        assertThat(pessoaFisicaList).hasSize(databaseSizeBeforeUpdate);
        PessoaFisica testPessoaFisica = pessoaFisicaList.get(pessoaFisicaList.size() - 1);
        assertThat(testPessoaFisica.getCpf()).isEqualTo(UPDATED_CPF);
        assertThat(testPessoaFisica.getRg()).isEqualTo(DEFAULT_RG);
        assertThat(testPessoaFisica.getDataNascimento()).isEqualTo(DEFAULT_DATA_NASCIMENTO);
        assertThat(testPessoaFisica.getIdade()).isEqualTo(UPDATED_IDADE);
        assertThat(testPessoaFisica.getSexo()).isEqualTo(UPDATED_SEXO);
        assertThat(testPessoaFisica.getCor()).isEqualTo(UPDATED_COR);
        assertThat(testPessoaFisica.getEstadoCivil()).isEqualTo(UPDATED_ESTADO_CIVIL);
        assertThat(testPessoaFisica.getNaturalidade()).isEqualTo(DEFAULT_NATURALIDADE);
        assertThat(testPessoaFisica.getNacionalidade()).isEqualTo(UPDATED_NACIONALIDADE);
    }

    @Test
    void fullUpdatePessoaFisicaWithPatch() throws Exception {
        // Initialize the database
        pessoaFisicaRepository.save(pessoaFisica).block();

        int databaseSizeBeforeUpdate = pessoaFisicaRepository.findAll().collectList().block().size();

        // Update the pessoaFisica using partial update
        PessoaFisica partialUpdatedPessoaFisica = new PessoaFisica();
        partialUpdatedPessoaFisica.setId(pessoaFisica.getId());

        partialUpdatedPessoaFisica
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
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaFisica.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaFisica))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaFisica in the database
        List<PessoaFisica> pessoaFisicaList = pessoaFisicaRepository.findAll().collectList().block();
        assertThat(pessoaFisicaList).hasSize(databaseSizeBeforeUpdate);
        PessoaFisica testPessoaFisica = pessoaFisicaList.get(pessoaFisicaList.size() - 1);
        assertThat(testPessoaFisica.getCpf()).isEqualTo(UPDATED_CPF);
        assertThat(testPessoaFisica.getRg()).isEqualTo(UPDATED_RG);
        assertThat(testPessoaFisica.getDataNascimento()).isEqualTo(UPDATED_DATA_NASCIMENTO);
        assertThat(testPessoaFisica.getIdade()).isEqualTo(UPDATED_IDADE);
        assertThat(testPessoaFisica.getSexo()).isEqualTo(UPDATED_SEXO);
        assertThat(testPessoaFisica.getCor()).isEqualTo(UPDATED_COR);
        assertThat(testPessoaFisica.getEstadoCivil()).isEqualTo(UPDATED_ESTADO_CIVIL);
        assertThat(testPessoaFisica.getNaturalidade()).isEqualTo(UPDATED_NATURALIDADE);
        assertThat(testPessoaFisica.getNacionalidade()).isEqualTo(UPDATED_NACIONALIDADE);
    }

    @Test
    void patchNonExistingPessoaFisica() throws Exception {
        int databaseSizeBeforeUpdate = pessoaFisicaRepository.findAll().collectList().block().size();
        pessoaFisica.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pessoaFisica.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFisica))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaFisica in the database
        List<PessoaFisica> pessoaFisicaList = pessoaFisicaRepository.findAll().collectList().block();
        assertThat(pessoaFisicaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPessoaFisica() throws Exception {
        int databaseSizeBeforeUpdate = pessoaFisicaRepository.findAll().collectList().block().size();
        pessoaFisica.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFisica))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaFisica in the database
        List<PessoaFisica> pessoaFisicaList = pessoaFisicaRepository.findAll().collectList().block();
        assertThat(pessoaFisicaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPessoaFisica() throws Exception {
        int databaseSizeBeforeUpdate = pessoaFisicaRepository.findAll().collectList().block().size();
        pessoaFisica.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaFisica))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaFisica in the database
        List<PessoaFisica> pessoaFisicaList = pessoaFisicaRepository.findAll().collectList().block();
        assertThat(pessoaFisicaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePessoaFisica() {
        // Initialize the database
        pessoaFisicaRepository.save(pessoaFisica).block();

        int databaseSizeBeforeDelete = pessoaFisicaRepository.findAll().collectList().block().size();

        // Delete the pessoaFisica
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pessoaFisica.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<PessoaFisica> pessoaFisicaList = pessoaFisicaRepository.findAll().collectList().block();
        assertThat(pessoaFisicaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
