package com.mboss.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mboss.core.IntegrationTest;
import com.mboss.core.domain.Criacao;
import com.mboss.core.repository.CriacaoRepository;
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
 * Integration tests for the {@link CriacaoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CriacaoResourceIT {

    private static final Long DEFAULT_ID_EMPRESA = 1L;
    private static final Long UPDATED_ID_EMPRESA = 2L;

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_SEXO = "AAAAAAAAAA";
    private static final String UPDATED_SEXO = "BBBBBBBBBB";

    private static final String DEFAULT_PORTE = "AAAAAAAAAA";
    private static final String UPDATED_PORTE = "BBBBBBBBBB";

    private static final Integer DEFAULT_IDADE = 1;
    private static final Integer UPDATED_IDADE = 2;

    private static final LocalDate DEFAULT_DATA_NASCIMENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_NASCIMENTO = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_CASTRADO = false;
    private static final Boolean UPDATED_CASTRADO = true;

    private static final String DEFAULT_ANOTACAO = "AAAAAAAAAA";
    private static final String UPDATED_ANOTACAO = "BBBBBBBBBB";

    private static final Boolean DEFAULT_PEDIGREE = false;
    private static final Boolean UPDATED_PEDIGREE = true;

    private static final Boolean DEFAULT_ATIVO = false;
    private static final Boolean UPDATED_ATIVO = true;

    private static final String ENTITY_API_URL = "/api/criacaos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CriacaoRepository criacaoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Criacao criacao;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Criacao createEntity(EntityManager em) {
        Criacao criacao = new Criacao()
            .idEmpresa(DEFAULT_ID_EMPRESA)
            .nome(DEFAULT_NOME)
            .sexo(DEFAULT_SEXO)
            .porte(DEFAULT_PORTE)
            .idade(DEFAULT_IDADE)
            .dataNascimento(DEFAULT_DATA_NASCIMENTO)
            .castrado(DEFAULT_CASTRADO)
            .anotacao(DEFAULT_ANOTACAO)
            .pedigree(DEFAULT_PEDIGREE)
            .ativo(DEFAULT_ATIVO);
        return criacao;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Criacao createUpdatedEntity(EntityManager em) {
        Criacao criacao = new Criacao()
            .idEmpresa(UPDATED_ID_EMPRESA)
            .nome(UPDATED_NOME)
            .sexo(UPDATED_SEXO)
            .porte(UPDATED_PORTE)
            .idade(UPDATED_IDADE)
            .dataNascimento(UPDATED_DATA_NASCIMENTO)
            .castrado(UPDATED_CASTRADO)
            .anotacao(UPDATED_ANOTACAO)
            .pedigree(UPDATED_PEDIGREE)
            .ativo(UPDATED_ATIVO);
        return criacao;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Criacao.class).block();
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
        criacao = createEntity(em);
    }

    @Test
    void createCriacao() throws Exception {
        int databaseSizeBeforeCreate = criacaoRepository.findAll().collectList().block().size();
        // Create the Criacao
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacao))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Criacao in the database
        List<Criacao> criacaoList = criacaoRepository.findAll().collectList().block();
        assertThat(criacaoList).hasSize(databaseSizeBeforeCreate + 1);
        Criacao testCriacao = criacaoList.get(criacaoList.size() - 1);
        assertThat(testCriacao.getIdEmpresa()).isEqualTo(DEFAULT_ID_EMPRESA);
        assertThat(testCriacao.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testCriacao.getSexo()).isEqualTo(DEFAULT_SEXO);
        assertThat(testCriacao.getPorte()).isEqualTo(DEFAULT_PORTE);
        assertThat(testCriacao.getIdade()).isEqualTo(DEFAULT_IDADE);
        assertThat(testCriacao.getDataNascimento()).isEqualTo(DEFAULT_DATA_NASCIMENTO);
        assertThat(testCriacao.getCastrado()).isEqualTo(DEFAULT_CASTRADO);
        assertThat(testCriacao.getAnotacao()).isEqualTo(DEFAULT_ANOTACAO);
        assertThat(testCriacao.getPedigree()).isEqualTo(DEFAULT_PEDIGREE);
        assertThat(testCriacao.getAtivo()).isEqualTo(DEFAULT_ATIVO);
    }

    @Test
    void createCriacaoWithExistingId() throws Exception {
        // Create the Criacao with an existing ID
        criacao.setId(1L);

        int databaseSizeBeforeCreate = criacaoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Criacao in the database
        List<Criacao> criacaoList = criacaoRepository.findAll().collectList().block();
        assertThat(criacaoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkIdEmpresaIsRequired() throws Exception {
        int databaseSizeBeforeTest = criacaoRepository.findAll().collectList().block().size();
        // set the field null
        criacao.setIdEmpresa(null);

        // Create the Criacao, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Criacao> criacaoList = criacaoRepository.findAll().collectList().block();
        assertThat(criacaoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = criacaoRepository.findAll().collectList().block().size();
        // set the field null
        criacao.setNome(null);

        // Create the Criacao, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Criacao> criacaoList = criacaoRepository.findAll().collectList().block();
        assertThat(criacaoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCriacaos() {
        // Initialize the database
        criacaoRepository.save(criacao).block();

        // Get all the criacaoList
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
            .value(hasItem(criacao.getId().intValue()))
            .jsonPath("$.[*].idEmpresa")
            .value(hasItem(DEFAULT_ID_EMPRESA.intValue()))
            .jsonPath("$.[*].nome")
            .value(hasItem(DEFAULT_NOME))
            .jsonPath("$.[*].sexo")
            .value(hasItem(DEFAULT_SEXO))
            .jsonPath("$.[*].porte")
            .value(hasItem(DEFAULT_PORTE))
            .jsonPath("$.[*].idade")
            .value(hasItem(DEFAULT_IDADE))
            .jsonPath("$.[*].dataNascimento")
            .value(hasItem(DEFAULT_DATA_NASCIMENTO.toString()))
            .jsonPath("$.[*].castrado")
            .value(hasItem(DEFAULT_CASTRADO.booleanValue()))
            .jsonPath("$.[*].anotacao")
            .value(hasItem(DEFAULT_ANOTACAO))
            .jsonPath("$.[*].pedigree")
            .value(hasItem(DEFAULT_PEDIGREE.booleanValue()))
            .jsonPath("$.[*].ativo")
            .value(hasItem(DEFAULT_ATIVO.booleanValue()));
    }

    @Test
    void getCriacao() {
        // Initialize the database
        criacaoRepository.save(criacao).block();

        // Get the criacao
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, criacao.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(criacao.getId().intValue()))
            .jsonPath("$.idEmpresa")
            .value(is(DEFAULT_ID_EMPRESA.intValue()))
            .jsonPath("$.nome")
            .value(is(DEFAULT_NOME))
            .jsonPath("$.sexo")
            .value(is(DEFAULT_SEXO))
            .jsonPath("$.porte")
            .value(is(DEFAULT_PORTE))
            .jsonPath("$.idade")
            .value(is(DEFAULT_IDADE))
            .jsonPath("$.dataNascimento")
            .value(is(DEFAULT_DATA_NASCIMENTO.toString()))
            .jsonPath("$.castrado")
            .value(is(DEFAULT_CASTRADO.booleanValue()))
            .jsonPath("$.anotacao")
            .value(is(DEFAULT_ANOTACAO))
            .jsonPath("$.pedigree")
            .value(is(DEFAULT_PEDIGREE.booleanValue()))
            .jsonPath("$.ativo")
            .value(is(DEFAULT_ATIVO.booleanValue()));
    }

    @Test
    void getNonExistingCriacao() {
        // Get the criacao
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCriacao() throws Exception {
        // Initialize the database
        criacaoRepository.save(criacao).block();

        int databaseSizeBeforeUpdate = criacaoRepository.findAll().collectList().block().size();

        // Update the criacao
        Criacao updatedCriacao = criacaoRepository.findById(criacao.getId()).block();
        updatedCriacao
            .idEmpresa(UPDATED_ID_EMPRESA)
            .nome(UPDATED_NOME)
            .sexo(UPDATED_SEXO)
            .porte(UPDATED_PORTE)
            .idade(UPDATED_IDADE)
            .dataNascimento(UPDATED_DATA_NASCIMENTO)
            .castrado(UPDATED_CASTRADO)
            .anotacao(UPDATED_ANOTACAO)
            .pedigree(UPDATED_PEDIGREE)
            .ativo(UPDATED_ATIVO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCriacao.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCriacao))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Criacao in the database
        List<Criacao> criacaoList = criacaoRepository.findAll().collectList().block();
        assertThat(criacaoList).hasSize(databaseSizeBeforeUpdate);
        Criacao testCriacao = criacaoList.get(criacaoList.size() - 1);
        assertThat(testCriacao.getIdEmpresa()).isEqualTo(UPDATED_ID_EMPRESA);
        assertThat(testCriacao.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testCriacao.getSexo()).isEqualTo(UPDATED_SEXO);
        assertThat(testCriacao.getPorte()).isEqualTo(UPDATED_PORTE);
        assertThat(testCriacao.getIdade()).isEqualTo(UPDATED_IDADE);
        assertThat(testCriacao.getDataNascimento()).isEqualTo(UPDATED_DATA_NASCIMENTO);
        assertThat(testCriacao.getCastrado()).isEqualTo(UPDATED_CASTRADO);
        assertThat(testCriacao.getAnotacao()).isEqualTo(UPDATED_ANOTACAO);
        assertThat(testCriacao.getPedigree()).isEqualTo(UPDATED_PEDIGREE);
        assertThat(testCriacao.getAtivo()).isEqualTo(UPDATED_ATIVO);
    }

    @Test
    void putNonExistingCriacao() throws Exception {
        int databaseSizeBeforeUpdate = criacaoRepository.findAll().collectList().block().size();
        criacao.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, criacao.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Criacao in the database
        List<Criacao> criacaoList = criacaoRepository.findAll().collectList().block();
        assertThat(criacaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCriacao() throws Exception {
        int databaseSizeBeforeUpdate = criacaoRepository.findAll().collectList().block().size();
        criacao.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Criacao in the database
        List<Criacao> criacaoList = criacaoRepository.findAll().collectList().block();
        assertThat(criacaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCriacao() throws Exception {
        int databaseSizeBeforeUpdate = criacaoRepository.findAll().collectList().block().size();
        criacao.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacao))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Criacao in the database
        List<Criacao> criacaoList = criacaoRepository.findAll().collectList().block();
        assertThat(criacaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCriacaoWithPatch() throws Exception {
        // Initialize the database
        criacaoRepository.save(criacao).block();

        int databaseSizeBeforeUpdate = criacaoRepository.findAll().collectList().block().size();

        // Update the criacao using partial update
        Criacao partialUpdatedCriacao = new Criacao();
        partialUpdatedCriacao.setId(criacao.getId());

        partialUpdatedCriacao
            .idEmpresa(UPDATED_ID_EMPRESA)
            .sexo(UPDATED_SEXO)
            .idade(UPDATED_IDADE)
            .dataNascimento(UPDATED_DATA_NASCIMENTO)
            .castrado(UPDATED_CASTRADO)
            .anotacao(UPDATED_ANOTACAO)
            .pedigree(UPDATED_PEDIGREE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCriacao.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCriacao))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Criacao in the database
        List<Criacao> criacaoList = criacaoRepository.findAll().collectList().block();
        assertThat(criacaoList).hasSize(databaseSizeBeforeUpdate);
        Criacao testCriacao = criacaoList.get(criacaoList.size() - 1);
        assertThat(testCriacao.getIdEmpresa()).isEqualTo(UPDATED_ID_EMPRESA);
        assertThat(testCriacao.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testCriacao.getSexo()).isEqualTo(UPDATED_SEXO);
        assertThat(testCriacao.getPorte()).isEqualTo(DEFAULT_PORTE);
        assertThat(testCriacao.getIdade()).isEqualTo(UPDATED_IDADE);
        assertThat(testCriacao.getDataNascimento()).isEqualTo(UPDATED_DATA_NASCIMENTO);
        assertThat(testCriacao.getCastrado()).isEqualTo(UPDATED_CASTRADO);
        assertThat(testCriacao.getAnotacao()).isEqualTo(UPDATED_ANOTACAO);
        assertThat(testCriacao.getPedigree()).isEqualTo(UPDATED_PEDIGREE);
        assertThat(testCriacao.getAtivo()).isEqualTo(DEFAULT_ATIVO);
    }

    @Test
    void fullUpdateCriacaoWithPatch() throws Exception {
        // Initialize the database
        criacaoRepository.save(criacao).block();

        int databaseSizeBeforeUpdate = criacaoRepository.findAll().collectList().block().size();

        // Update the criacao using partial update
        Criacao partialUpdatedCriacao = new Criacao();
        partialUpdatedCriacao.setId(criacao.getId());

        partialUpdatedCriacao
            .idEmpresa(UPDATED_ID_EMPRESA)
            .nome(UPDATED_NOME)
            .sexo(UPDATED_SEXO)
            .porte(UPDATED_PORTE)
            .idade(UPDATED_IDADE)
            .dataNascimento(UPDATED_DATA_NASCIMENTO)
            .castrado(UPDATED_CASTRADO)
            .anotacao(UPDATED_ANOTACAO)
            .pedigree(UPDATED_PEDIGREE)
            .ativo(UPDATED_ATIVO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCriacao.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCriacao))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Criacao in the database
        List<Criacao> criacaoList = criacaoRepository.findAll().collectList().block();
        assertThat(criacaoList).hasSize(databaseSizeBeforeUpdate);
        Criacao testCriacao = criacaoList.get(criacaoList.size() - 1);
        assertThat(testCriacao.getIdEmpresa()).isEqualTo(UPDATED_ID_EMPRESA);
        assertThat(testCriacao.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testCriacao.getSexo()).isEqualTo(UPDATED_SEXO);
        assertThat(testCriacao.getPorte()).isEqualTo(UPDATED_PORTE);
        assertThat(testCriacao.getIdade()).isEqualTo(UPDATED_IDADE);
        assertThat(testCriacao.getDataNascimento()).isEqualTo(UPDATED_DATA_NASCIMENTO);
        assertThat(testCriacao.getCastrado()).isEqualTo(UPDATED_CASTRADO);
        assertThat(testCriacao.getAnotacao()).isEqualTo(UPDATED_ANOTACAO);
        assertThat(testCriacao.getPedigree()).isEqualTo(UPDATED_PEDIGREE);
        assertThat(testCriacao.getAtivo()).isEqualTo(UPDATED_ATIVO);
    }

    @Test
    void patchNonExistingCriacao() throws Exception {
        int databaseSizeBeforeUpdate = criacaoRepository.findAll().collectList().block().size();
        criacao.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, criacao.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Criacao in the database
        List<Criacao> criacaoList = criacaoRepository.findAll().collectList().block();
        assertThat(criacaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCriacao() throws Exception {
        int databaseSizeBeforeUpdate = criacaoRepository.findAll().collectList().block().size();
        criacao.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Criacao in the database
        List<Criacao> criacaoList = criacaoRepository.findAll().collectList().block();
        assertThat(criacaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCriacao() throws Exception {
        int databaseSizeBeforeUpdate = criacaoRepository.findAll().collectList().block().size();
        criacao.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacao))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Criacao in the database
        List<Criacao> criacaoList = criacaoRepository.findAll().collectList().block();
        assertThat(criacaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCriacao() {
        // Initialize the database
        criacaoRepository.save(criacao).block();

        int databaseSizeBeforeDelete = criacaoRepository.findAll().collectList().block().size();

        // Delete the criacao
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, criacao.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Criacao> criacaoList = criacaoRepository.findAll().collectList().block();
        assertThat(criacaoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
