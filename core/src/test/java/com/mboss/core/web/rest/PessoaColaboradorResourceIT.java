package com.mboss.core.web.rest;

import static com.mboss.core.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mboss.core.IntegrationTest;
import com.mboss.core.domain.PessoaColaborador;
import com.mboss.core.repository.PessoaColaboradorRepository;
import com.mboss.core.service.EntityManager;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link PessoaColaboradorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PessoaColaboradorResourceIT {

    private static final LocalDate DEFAULT_DATA_ADIMISSAO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_ADIMISSAO = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATA_SAIDA = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_SAIDA = LocalDate.now(ZoneId.systemDefault());

    private static final Long DEFAULT_CARGA_HORARIA = 1L;
    private static final Long UPDATED_CARGA_HORARIA = 2L;

    private static final ZonedDateTime DEFAULT_PRIMEIRO_HORARIO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_PRIMEIRO_HORARIO = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_SEGUNDO_HORARIO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_SEGUNDO_HORARIO = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Double DEFAULT_SALARIO = 1D;
    private static final Double UPDATED_SALARIO = 2D;

    private static final Double DEFAULT_COMISSAO = 1D;
    private static final Double UPDATED_COMISSAO = 2D;

    private static final Double DEFAULT_DESCONTO_MAXIMO = 1D;
    private static final Double UPDATED_DESCONTO_MAXIMO = 2D;

    private static final String ENTITY_API_URL = "/api/pessoa-colaboradors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PessoaColaboradorRepository pessoaColaboradorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PessoaColaborador pessoaColaborador;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaColaborador createEntity(EntityManager em) {
        PessoaColaborador pessoaColaborador = new PessoaColaborador()
            .dataAdimissao(DEFAULT_DATA_ADIMISSAO)
            .dataSaida(DEFAULT_DATA_SAIDA)
            .cargaHoraria(DEFAULT_CARGA_HORARIA)
            .primeiroHorario(DEFAULT_PRIMEIRO_HORARIO)
            .segundoHorario(DEFAULT_SEGUNDO_HORARIO)
            .salario(DEFAULT_SALARIO)
            .comissao(DEFAULT_COMISSAO)
            .descontoMaximo(DEFAULT_DESCONTO_MAXIMO);
        return pessoaColaborador;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaColaborador createUpdatedEntity(EntityManager em) {
        PessoaColaborador pessoaColaborador = new PessoaColaborador()
            .dataAdimissao(UPDATED_DATA_ADIMISSAO)
            .dataSaida(UPDATED_DATA_SAIDA)
            .cargaHoraria(UPDATED_CARGA_HORARIA)
            .primeiroHorario(UPDATED_PRIMEIRO_HORARIO)
            .segundoHorario(UPDATED_SEGUNDO_HORARIO)
            .salario(UPDATED_SALARIO)
            .comissao(UPDATED_COMISSAO)
            .descontoMaximo(UPDATED_DESCONTO_MAXIMO);
        return pessoaColaborador;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PessoaColaborador.class).block();
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
        pessoaColaborador = createEntity(em);
    }

    @Test
    void createPessoaColaborador() throws Exception {
        int databaseSizeBeforeCreate = pessoaColaboradorRepository.findAll().collectList().block().size();
        // Create the PessoaColaborador
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaColaborador))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the PessoaColaborador in the database
        List<PessoaColaborador> pessoaColaboradorList = pessoaColaboradorRepository.findAll().collectList().block();
        assertThat(pessoaColaboradorList).hasSize(databaseSizeBeforeCreate + 1);
        PessoaColaborador testPessoaColaborador = pessoaColaboradorList.get(pessoaColaboradorList.size() - 1);
        assertThat(testPessoaColaborador.getDataAdimissao()).isEqualTo(DEFAULT_DATA_ADIMISSAO);
        assertThat(testPessoaColaborador.getDataSaida()).isEqualTo(DEFAULT_DATA_SAIDA);
        assertThat(testPessoaColaborador.getCargaHoraria()).isEqualTo(DEFAULT_CARGA_HORARIA);
        assertThat(testPessoaColaborador.getPrimeiroHorario()).isEqualTo(DEFAULT_PRIMEIRO_HORARIO);
        assertThat(testPessoaColaborador.getSegundoHorario()).isEqualTo(DEFAULT_SEGUNDO_HORARIO);
        assertThat(testPessoaColaborador.getSalario()).isEqualTo(DEFAULT_SALARIO);
        assertThat(testPessoaColaborador.getComissao()).isEqualTo(DEFAULT_COMISSAO);
        assertThat(testPessoaColaborador.getDescontoMaximo()).isEqualTo(DEFAULT_DESCONTO_MAXIMO);
    }

    @Test
    void createPessoaColaboradorWithExistingId() throws Exception {
        // Create the PessoaColaborador with an existing ID
        pessoaColaborador.setId(1L);

        int databaseSizeBeforeCreate = pessoaColaboradorRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaColaborador))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaColaborador in the database
        List<PessoaColaborador> pessoaColaboradorList = pessoaColaboradorRepository.findAll().collectList().block();
        assertThat(pessoaColaboradorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPessoaColaboradorsAsStream() {
        // Initialize the database
        pessoaColaboradorRepository.save(pessoaColaborador).block();

        List<PessoaColaborador> pessoaColaboradorList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PessoaColaborador.class)
            .getResponseBody()
            .filter(pessoaColaborador::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(pessoaColaboradorList).isNotNull();
        assertThat(pessoaColaboradorList).hasSize(1);
        PessoaColaborador testPessoaColaborador = pessoaColaboradorList.get(0);
        assertThat(testPessoaColaborador.getDataAdimissao()).isEqualTo(DEFAULT_DATA_ADIMISSAO);
        assertThat(testPessoaColaborador.getDataSaida()).isEqualTo(DEFAULT_DATA_SAIDA);
        assertThat(testPessoaColaborador.getCargaHoraria()).isEqualTo(DEFAULT_CARGA_HORARIA);
        assertThat(testPessoaColaborador.getPrimeiroHorario()).isEqualTo(DEFAULT_PRIMEIRO_HORARIO);
        assertThat(testPessoaColaborador.getSegundoHorario()).isEqualTo(DEFAULT_SEGUNDO_HORARIO);
        assertThat(testPessoaColaborador.getSalario()).isEqualTo(DEFAULT_SALARIO);
        assertThat(testPessoaColaborador.getComissao()).isEqualTo(DEFAULT_COMISSAO);
        assertThat(testPessoaColaborador.getDescontoMaximo()).isEqualTo(DEFAULT_DESCONTO_MAXIMO);
    }

    @Test
    void getAllPessoaColaboradors() {
        // Initialize the database
        pessoaColaboradorRepository.save(pessoaColaborador).block();

        // Get all the pessoaColaboradorList
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
            .value(hasItem(pessoaColaborador.getId().intValue()))
            .jsonPath("$.[*].dataAdimissao")
            .value(hasItem(DEFAULT_DATA_ADIMISSAO.toString()))
            .jsonPath("$.[*].dataSaida")
            .value(hasItem(DEFAULT_DATA_SAIDA.toString()))
            .jsonPath("$.[*].cargaHoraria")
            .value(hasItem(DEFAULT_CARGA_HORARIA.intValue()))
            .jsonPath("$.[*].primeiroHorario")
            .value(hasItem(sameInstant(DEFAULT_PRIMEIRO_HORARIO)))
            .jsonPath("$.[*].segundoHorario")
            .value(hasItem(sameInstant(DEFAULT_SEGUNDO_HORARIO)))
            .jsonPath("$.[*].salario")
            .value(hasItem(DEFAULT_SALARIO.doubleValue()))
            .jsonPath("$.[*].comissao")
            .value(hasItem(DEFAULT_COMISSAO.doubleValue()))
            .jsonPath("$.[*].descontoMaximo")
            .value(hasItem(DEFAULT_DESCONTO_MAXIMO.doubleValue()));
    }

    @Test
    void getPessoaColaborador() {
        // Initialize the database
        pessoaColaboradorRepository.save(pessoaColaborador).block();

        // Get the pessoaColaborador
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pessoaColaborador.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pessoaColaborador.getId().intValue()))
            .jsonPath("$.dataAdimissao")
            .value(is(DEFAULT_DATA_ADIMISSAO.toString()))
            .jsonPath("$.dataSaida")
            .value(is(DEFAULT_DATA_SAIDA.toString()))
            .jsonPath("$.cargaHoraria")
            .value(is(DEFAULT_CARGA_HORARIA.intValue()))
            .jsonPath("$.primeiroHorario")
            .value(is(sameInstant(DEFAULT_PRIMEIRO_HORARIO)))
            .jsonPath("$.segundoHorario")
            .value(is(sameInstant(DEFAULT_SEGUNDO_HORARIO)))
            .jsonPath("$.salario")
            .value(is(DEFAULT_SALARIO.doubleValue()))
            .jsonPath("$.comissao")
            .value(is(DEFAULT_COMISSAO.doubleValue()))
            .jsonPath("$.descontoMaximo")
            .value(is(DEFAULT_DESCONTO_MAXIMO.doubleValue()));
    }

    @Test
    void getNonExistingPessoaColaborador() {
        // Get the pessoaColaborador
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPessoaColaborador() throws Exception {
        // Initialize the database
        pessoaColaboradorRepository.save(pessoaColaborador).block();

        int databaseSizeBeforeUpdate = pessoaColaboradorRepository.findAll().collectList().block().size();

        // Update the pessoaColaborador
        PessoaColaborador updatedPessoaColaborador = pessoaColaboradorRepository.findById(pessoaColaborador.getId()).block();
        updatedPessoaColaborador
            .dataAdimissao(UPDATED_DATA_ADIMISSAO)
            .dataSaida(UPDATED_DATA_SAIDA)
            .cargaHoraria(UPDATED_CARGA_HORARIA)
            .primeiroHorario(UPDATED_PRIMEIRO_HORARIO)
            .segundoHorario(UPDATED_SEGUNDO_HORARIO)
            .salario(UPDATED_SALARIO)
            .comissao(UPDATED_COMISSAO)
            .descontoMaximo(UPDATED_DESCONTO_MAXIMO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPessoaColaborador.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPessoaColaborador))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaColaborador in the database
        List<PessoaColaborador> pessoaColaboradorList = pessoaColaboradorRepository.findAll().collectList().block();
        assertThat(pessoaColaboradorList).hasSize(databaseSizeBeforeUpdate);
        PessoaColaborador testPessoaColaborador = pessoaColaboradorList.get(pessoaColaboradorList.size() - 1);
        assertThat(testPessoaColaborador.getDataAdimissao()).isEqualTo(UPDATED_DATA_ADIMISSAO);
        assertThat(testPessoaColaborador.getDataSaida()).isEqualTo(UPDATED_DATA_SAIDA);
        assertThat(testPessoaColaborador.getCargaHoraria()).isEqualTo(UPDATED_CARGA_HORARIA);
        assertThat(testPessoaColaborador.getPrimeiroHorario()).isEqualTo(UPDATED_PRIMEIRO_HORARIO);
        assertThat(testPessoaColaborador.getSegundoHorario()).isEqualTo(UPDATED_SEGUNDO_HORARIO);
        assertThat(testPessoaColaborador.getSalario()).isEqualTo(UPDATED_SALARIO);
        assertThat(testPessoaColaborador.getComissao()).isEqualTo(UPDATED_COMISSAO);
        assertThat(testPessoaColaborador.getDescontoMaximo()).isEqualTo(UPDATED_DESCONTO_MAXIMO);
    }

    @Test
    void putNonExistingPessoaColaborador() throws Exception {
        int databaseSizeBeforeUpdate = pessoaColaboradorRepository.findAll().collectList().block().size();
        pessoaColaborador.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pessoaColaborador.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaColaborador))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaColaborador in the database
        List<PessoaColaborador> pessoaColaboradorList = pessoaColaboradorRepository.findAll().collectList().block();
        assertThat(pessoaColaboradorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPessoaColaborador() throws Exception {
        int databaseSizeBeforeUpdate = pessoaColaboradorRepository.findAll().collectList().block().size();
        pessoaColaborador.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaColaborador))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaColaborador in the database
        List<PessoaColaborador> pessoaColaboradorList = pessoaColaboradorRepository.findAll().collectList().block();
        assertThat(pessoaColaboradorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPessoaColaborador() throws Exception {
        int databaseSizeBeforeUpdate = pessoaColaboradorRepository.findAll().collectList().block().size();
        pessoaColaborador.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaColaborador))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaColaborador in the database
        List<PessoaColaborador> pessoaColaboradorList = pessoaColaboradorRepository.findAll().collectList().block();
        assertThat(pessoaColaboradorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePessoaColaboradorWithPatch() throws Exception {
        // Initialize the database
        pessoaColaboradorRepository.save(pessoaColaborador).block();

        int databaseSizeBeforeUpdate = pessoaColaboradorRepository.findAll().collectList().block().size();

        // Update the pessoaColaborador using partial update
        PessoaColaborador partialUpdatedPessoaColaborador = new PessoaColaborador();
        partialUpdatedPessoaColaborador.setId(pessoaColaborador.getId());

        partialUpdatedPessoaColaborador.dataAdimissao(UPDATED_DATA_ADIMISSAO).primeiroHorario(UPDATED_PRIMEIRO_HORARIO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaColaborador.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaColaborador))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaColaborador in the database
        List<PessoaColaborador> pessoaColaboradorList = pessoaColaboradorRepository.findAll().collectList().block();
        assertThat(pessoaColaboradorList).hasSize(databaseSizeBeforeUpdate);
        PessoaColaborador testPessoaColaborador = pessoaColaboradorList.get(pessoaColaboradorList.size() - 1);
        assertThat(testPessoaColaborador.getDataAdimissao()).isEqualTo(UPDATED_DATA_ADIMISSAO);
        assertThat(testPessoaColaborador.getDataSaida()).isEqualTo(DEFAULT_DATA_SAIDA);
        assertThat(testPessoaColaborador.getCargaHoraria()).isEqualTo(DEFAULT_CARGA_HORARIA);
        assertThat(testPessoaColaborador.getPrimeiroHorario()).isEqualTo(UPDATED_PRIMEIRO_HORARIO);
        assertThat(testPessoaColaborador.getSegundoHorario()).isEqualTo(DEFAULT_SEGUNDO_HORARIO);
        assertThat(testPessoaColaborador.getSalario()).isEqualTo(DEFAULT_SALARIO);
        assertThat(testPessoaColaborador.getComissao()).isEqualTo(DEFAULT_COMISSAO);
        assertThat(testPessoaColaborador.getDescontoMaximo()).isEqualTo(DEFAULT_DESCONTO_MAXIMO);
    }

    @Test
    void fullUpdatePessoaColaboradorWithPatch() throws Exception {
        // Initialize the database
        pessoaColaboradorRepository.save(pessoaColaborador).block();

        int databaseSizeBeforeUpdate = pessoaColaboradorRepository.findAll().collectList().block().size();

        // Update the pessoaColaborador using partial update
        PessoaColaborador partialUpdatedPessoaColaborador = new PessoaColaborador();
        partialUpdatedPessoaColaborador.setId(pessoaColaborador.getId());

        partialUpdatedPessoaColaborador
            .dataAdimissao(UPDATED_DATA_ADIMISSAO)
            .dataSaida(UPDATED_DATA_SAIDA)
            .cargaHoraria(UPDATED_CARGA_HORARIA)
            .primeiroHorario(UPDATED_PRIMEIRO_HORARIO)
            .segundoHorario(UPDATED_SEGUNDO_HORARIO)
            .salario(UPDATED_SALARIO)
            .comissao(UPDATED_COMISSAO)
            .descontoMaximo(UPDATED_DESCONTO_MAXIMO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaColaborador.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaColaborador))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaColaborador in the database
        List<PessoaColaborador> pessoaColaboradorList = pessoaColaboradorRepository.findAll().collectList().block();
        assertThat(pessoaColaboradorList).hasSize(databaseSizeBeforeUpdate);
        PessoaColaborador testPessoaColaborador = pessoaColaboradorList.get(pessoaColaboradorList.size() - 1);
        assertThat(testPessoaColaborador.getDataAdimissao()).isEqualTo(UPDATED_DATA_ADIMISSAO);
        assertThat(testPessoaColaborador.getDataSaida()).isEqualTo(UPDATED_DATA_SAIDA);
        assertThat(testPessoaColaborador.getCargaHoraria()).isEqualTo(UPDATED_CARGA_HORARIA);
        assertThat(testPessoaColaborador.getPrimeiroHorario()).isEqualTo(UPDATED_PRIMEIRO_HORARIO);
        assertThat(testPessoaColaborador.getSegundoHorario()).isEqualTo(UPDATED_SEGUNDO_HORARIO);
        assertThat(testPessoaColaborador.getSalario()).isEqualTo(UPDATED_SALARIO);
        assertThat(testPessoaColaborador.getComissao()).isEqualTo(UPDATED_COMISSAO);
        assertThat(testPessoaColaborador.getDescontoMaximo()).isEqualTo(UPDATED_DESCONTO_MAXIMO);
    }

    @Test
    void patchNonExistingPessoaColaborador() throws Exception {
        int databaseSizeBeforeUpdate = pessoaColaboradorRepository.findAll().collectList().block().size();
        pessoaColaborador.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pessoaColaborador.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaColaborador))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaColaborador in the database
        List<PessoaColaborador> pessoaColaboradorList = pessoaColaboradorRepository.findAll().collectList().block();
        assertThat(pessoaColaboradorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPessoaColaborador() throws Exception {
        int databaseSizeBeforeUpdate = pessoaColaboradorRepository.findAll().collectList().block().size();
        pessoaColaborador.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaColaborador))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaColaborador in the database
        List<PessoaColaborador> pessoaColaboradorList = pessoaColaboradorRepository.findAll().collectList().block();
        assertThat(pessoaColaboradorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPessoaColaborador() throws Exception {
        int databaseSizeBeforeUpdate = pessoaColaboradorRepository.findAll().collectList().block().size();
        pessoaColaborador.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaColaborador))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaColaborador in the database
        List<PessoaColaborador> pessoaColaboradorList = pessoaColaboradorRepository.findAll().collectList().block();
        assertThat(pessoaColaboradorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePessoaColaborador() {
        // Initialize the database
        pessoaColaboradorRepository.save(pessoaColaborador).block();

        int databaseSizeBeforeDelete = pessoaColaboradorRepository.findAll().collectList().block().size();

        // Delete the pessoaColaborador
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pessoaColaborador.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<PessoaColaborador> pessoaColaboradorList = pessoaColaboradorRepository.findAll().collectList().block();
        assertThat(pessoaColaboradorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
