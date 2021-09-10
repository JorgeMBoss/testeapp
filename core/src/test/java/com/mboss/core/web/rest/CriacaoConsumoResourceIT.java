package com.mboss.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mboss.core.IntegrationTest;
import com.mboss.core.domain.CriacaoConsumo;
import com.mboss.core.repository.CriacaoConsumoRepository;
import com.mboss.core.service.EntityManager;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link CriacaoConsumoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class CriacaoConsumoResourceIT {

    private static final LocalDate DEFAULT_DATA_SISTEMA = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_SISTEMA = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATA_VENDA = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_VENDA = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATA_AVISO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_AVISO = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_ANOTACAO = "AAAAAAAAAA";
    private static final String UPDATED_ANOTACAO = "BBBBBBBBBB";

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String ENTITY_API_URL = "/api/criacao-consumos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CriacaoConsumoRepository criacaoConsumoRepository;

    @Mock
    private CriacaoConsumoRepository criacaoConsumoRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private CriacaoConsumo criacaoConsumo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CriacaoConsumo createEntity(EntityManager em) {
        CriacaoConsumo criacaoConsumo = new CriacaoConsumo()
            .dataSistema(DEFAULT_DATA_SISTEMA)
            .dataVenda(DEFAULT_DATA_VENDA)
            .dataAviso(DEFAULT_DATA_AVISO)
            .anotacao(DEFAULT_ANOTACAO)
            .status(DEFAULT_STATUS);
        return criacaoConsumo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CriacaoConsumo createUpdatedEntity(EntityManager em) {
        CriacaoConsumo criacaoConsumo = new CriacaoConsumo()
            .dataSistema(UPDATED_DATA_SISTEMA)
            .dataVenda(UPDATED_DATA_VENDA)
            .dataAviso(UPDATED_DATA_AVISO)
            .anotacao(UPDATED_ANOTACAO)
            .status(UPDATED_STATUS);
        return criacaoConsumo;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_criacao_consumo__criacao").block();
            em.deleteAll(CriacaoConsumo.class).block();
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
        criacaoConsumo = createEntity(em);
    }

    @Test
    void createCriacaoConsumo() throws Exception {
        int databaseSizeBeforeCreate = criacaoConsumoRepository.findAll().collectList().block().size();
        // Create the CriacaoConsumo
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoConsumo))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the CriacaoConsumo in the database
        List<CriacaoConsumo> criacaoConsumoList = criacaoConsumoRepository.findAll().collectList().block();
        assertThat(criacaoConsumoList).hasSize(databaseSizeBeforeCreate + 1);
        CriacaoConsumo testCriacaoConsumo = criacaoConsumoList.get(criacaoConsumoList.size() - 1);
        assertThat(testCriacaoConsumo.getDataSistema()).isEqualTo(DEFAULT_DATA_SISTEMA);
        assertThat(testCriacaoConsumo.getDataVenda()).isEqualTo(DEFAULT_DATA_VENDA);
        assertThat(testCriacaoConsumo.getDataAviso()).isEqualTo(DEFAULT_DATA_AVISO);
        assertThat(testCriacaoConsumo.getAnotacao()).isEqualTo(DEFAULT_ANOTACAO);
        assertThat(testCriacaoConsumo.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createCriacaoConsumoWithExistingId() throws Exception {
        // Create the CriacaoConsumo with an existing ID
        criacaoConsumo.setId(1L);

        int databaseSizeBeforeCreate = criacaoConsumoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoConsumo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoConsumo in the database
        List<CriacaoConsumo> criacaoConsumoList = criacaoConsumoRepository.findAll().collectList().block();
        assertThat(criacaoConsumoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCriacaoConsumos() {
        // Initialize the database
        criacaoConsumoRepository.save(criacaoConsumo).block();

        // Get all the criacaoConsumoList
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
            .value(hasItem(criacaoConsumo.getId().intValue()))
            .jsonPath("$.[*].dataSistema")
            .value(hasItem(DEFAULT_DATA_SISTEMA.toString()))
            .jsonPath("$.[*].dataVenda")
            .value(hasItem(DEFAULT_DATA_VENDA.toString()))
            .jsonPath("$.[*].dataAviso")
            .value(hasItem(DEFAULT_DATA_AVISO.toString()))
            .jsonPath("$.[*].anotacao")
            .value(hasItem(DEFAULT_ANOTACAO))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.booleanValue()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCriacaoConsumosWithEagerRelationshipsIsEnabled() {
        when(criacaoConsumoRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(criacaoConsumoRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCriacaoConsumosWithEagerRelationshipsIsNotEnabled() {
        when(criacaoConsumoRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(criacaoConsumoRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getCriacaoConsumo() {
        // Initialize the database
        criacaoConsumoRepository.save(criacaoConsumo).block();

        // Get the criacaoConsumo
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, criacaoConsumo.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(criacaoConsumo.getId().intValue()))
            .jsonPath("$.dataSistema")
            .value(is(DEFAULT_DATA_SISTEMA.toString()))
            .jsonPath("$.dataVenda")
            .value(is(DEFAULT_DATA_VENDA.toString()))
            .jsonPath("$.dataAviso")
            .value(is(DEFAULT_DATA_AVISO.toString()))
            .jsonPath("$.anotacao")
            .value(is(DEFAULT_ANOTACAO))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.booleanValue()));
    }

    @Test
    void getNonExistingCriacaoConsumo() {
        // Get the criacaoConsumo
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCriacaoConsumo() throws Exception {
        // Initialize the database
        criacaoConsumoRepository.save(criacaoConsumo).block();

        int databaseSizeBeforeUpdate = criacaoConsumoRepository.findAll().collectList().block().size();

        // Update the criacaoConsumo
        CriacaoConsumo updatedCriacaoConsumo = criacaoConsumoRepository.findById(criacaoConsumo.getId()).block();
        updatedCriacaoConsumo
            .dataSistema(UPDATED_DATA_SISTEMA)
            .dataVenda(UPDATED_DATA_VENDA)
            .dataAviso(UPDATED_DATA_AVISO)
            .anotacao(UPDATED_ANOTACAO)
            .status(UPDATED_STATUS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCriacaoConsumo.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCriacaoConsumo))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CriacaoConsumo in the database
        List<CriacaoConsumo> criacaoConsumoList = criacaoConsumoRepository.findAll().collectList().block();
        assertThat(criacaoConsumoList).hasSize(databaseSizeBeforeUpdate);
        CriacaoConsumo testCriacaoConsumo = criacaoConsumoList.get(criacaoConsumoList.size() - 1);
        assertThat(testCriacaoConsumo.getDataSistema()).isEqualTo(UPDATED_DATA_SISTEMA);
        assertThat(testCriacaoConsumo.getDataVenda()).isEqualTo(UPDATED_DATA_VENDA);
        assertThat(testCriacaoConsumo.getDataAviso()).isEqualTo(UPDATED_DATA_AVISO);
        assertThat(testCriacaoConsumo.getAnotacao()).isEqualTo(UPDATED_ANOTACAO);
        assertThat(testCriacaoConsumo.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void putNonExistingCriacaoConsumo() throws Exception {
        int databaseSizeBeforeUpdate = criacaoConsumoRepository.findAll().collectList().block().size();
        criacaoConsumo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, criacaoConsumo.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoConsumo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoConsumo in the database
        List<CriacaoConsumo> criacaoConsumoList = criacaoConsumoRepository.findAll().collectList().block();
        assertThat(criacaoConsumoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCriacaoConsumo() throws Exception {
        int databaseSizeBeforeUpdate = criacaoConsumoRepository.findAll().collectList().block().size();
        criacaoConsumo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoConsumo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoConsumo in the database
        List<CriacaoConsumo> criacaoConsumoList = criacaoConsumoRepository.findAll().collectList().block();
        assertThat(criacaoConsumoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCriacaoConsumo() throws Exception {
        int databaseSizeBeforeUpdate = criacaoConsumoRepository.findAll().collectList().block().size();
        criacaoConsumo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoConsumo))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CriacaoConsumo in the database
        List<CriacaoConsumo> criacaoConsumoList = criacaoConsumoRepository.findAll().collectList().block();
        assertThat(criacaoConsumoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCriacaoConsumoWithPatch() throws Exception {
        // Initialize the database
        criacaoConsumoRepository.save(criacaoConsumo).block();

        int databaseSizeBeforeUpdate = criacaoConsumoRepository.findAll().collectList().block().size();

        // Update the criacaoConsumo using partial update
        CriacaoConsumo partialUpdatedCriacaoConsumo = new CriacaoConsumo();
        partialUpdatedCriacaoConsumo.setId(criacaoConsumo.getId());

        partialUpdatedCriacaoConsumo.dataSistema(UPDATED_DATA_SISTEMA).dataVenda(UPDATED_DATA_VENDA).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCriacaoConsumo.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCriacaoConsumo))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CriacaoConsumo in the database
        List<CriacaoConsumo> criacaoConsumoList = criacaoConsumoRepository.findAll().collectList().block();
        assertThat(criacaoConsumoList).hasSize(databaseSizeBeforeUpdate);
        CriacaoConsumo testCriacaoConsumo = criacaoConsumoList.get(criacaoConsumoList.size() - 1);
        assertThat(testCriacaoConsumo.getDataSistema()).isEqualTo(UPDATED_DATA_SISTEMA);
        assertThat(testCriacaoConsumo.getDataVenda()).isEqualTo(UPDATED_DATA_VENDA);
        assertThat(testCriacaoConsumo.getDataAviso()).isEqualTo(DEFAULT_DATA_AVISO);
        assertThat(testCriacaoConsumo.getAnotacao()).isEqualTo(DEFAULT_ANOTACAO);
        assertThat(testCriacaoConsumo.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void fullUpdateCriacaoConsumoWithPatch() throws Exception {
        // Initialize the database
        criacaoConsumoRepository.save(criacaoConsumo).block();

        int databaseSizeBeforeUpdate = criacaoConsumoRepository.findAll().collectList().block().size();

        // Update the criacaoConsumo using partial update
        CriacaoConsumo partialUpdatedCriacaoConsumo = new CriacaoConsumo();
        partialUpdatedCriacaoConsumo.setId(criacaoConsumo.getId());

        partialUpdatedCriacaoConsumo
            .dataSistema(UPDATED_DATA_SISTEMA)
            .dataVenda(UPDATED_DATA_VENDA)
            .dataAviso(UPDATED_DATA_AVISO)
            .anotacao(UPDATED_ANOTACAO)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCriacaoConsumo.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCriacaoConsumo))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CriacaoConsumo in the database
        List<CriacaoConsumo> criacaoConsumoList = criacaoConsumoRepository.findAll().collectList().block();
        assertThat(criacaoConsumoList).hasSize(databaseSizeBeforeUpdate);
        CriacaoConsumo testCriacaoConsumo = criacaoConsumoList.get(criacaoConsumoList.size() - 1);
        assertThat(testCriacaoConsumo.getDataSistema()).isEqualTo(UPDATED_DATA_SISTEMA);
        assertThat(testCriacaoConsumo.getDataVenda()).isEqualTo(UPDATED_DATA_VENDA);
        assertThat(testCriacaoConsumo.getDataAviso()).isEqualTo(UPDATED_DATA_AVISO);
        assertThat(testCriacaoConsumo.getAnotacao()).isEqualTo(UPDATED_ANOTACAO);
        assertThat(testCriacaoConsumo.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingCriacaoConsumo() throws Exception {
        int databaseSizeBeforeUpdate = criacaoConsumoRepository.findAll().collectList().block().size();
        criacaoConsumo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, criacaoConsumo.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoConsumo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoConsumo in the database
        List<CriacaoConsumo> criacaoConsumoList = criacaoConsumoRepository.findAll().collectList().block();
        assertThat(criacaoConsumoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCriacaoConsumo() throws Exception {
        int databaseSizeBeforeUpdate = criacaoConsumoRepository.findAll().collectList().block().size();
        criacaoConsumo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoConsumo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoConsumo in the database
        List<CriacaoConsumo> criacaoConsumoList = criacaoConsumoRepository.findAll().collectList().block();
        assertThat(criacaoConsumoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCriacaoConsumo() throws Exception {
        int databaseSizeBeforeUpdate = criacaoConsumoRepository.findAll().collectList().block().size();
        criacaoConsumo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoConsumo))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CriacaoConsumo in the database
        List<CriacaoConsumo> criacaoConsumoList = criacaoConsumoRepository.findAll().collectList().block();
        assertThat(criacaoConsumoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCriacaoConsumo() {
        // Initialize the database
        criacaoConsumoRepository.save(criacaoConsumo).block();

        int databaseSizeBeforeDelete = criacaoConsumoRepository.findAll().collectList().block().size();

        // Delete the criacaoConsumo
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, criacaoConsumo.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<CriacaoConsumo> criacaoConsumoList = criacaoConsumoRepository.findAll().collectList().block();
        assertThat(criacaoConsumoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
