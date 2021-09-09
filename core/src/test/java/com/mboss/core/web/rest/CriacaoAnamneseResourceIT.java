package com.mboss.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mboss.core.IntegrationTest;
import com.mboss.core.domain.CriacaoAnamnese;
import com.mboss.core.repository.CriacaoAnamneseRepository;
import com.mboss.core.service.EntityManager;
import java.time.Duration;
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
 * Integration tests for the {@link CriacaoAnamneseResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class CriacaoAnamneseResourceIT {

    private static final String DEFAULT_DESCRICAO = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBBBBBBB";

    private static final Long DEFAULT_ID_MEDICO = 1L;
    private static final Long UPDATED_ID_MEDICO = 2L;

    private static final String ENTITY_API_URL = "/api/criacao-anamnese";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CriacaoAnamneseRepository criacaoAnamneseRepository;

    @Mock
    private CriacaoAnamneseRepository criacaoAnamneseRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private CriacaoAnamnese criacaoAnamnese;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CriacaoAnamnese createEntity(EntityManager em) {
        CriacaoAnamnese criacaoAnamnese = new CriacaoAnamnese().descricao(DEFAULT_DESCRICAO).idMedico(DEFAULT_ID_MEDICO);
        return criacaoAnamnese;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CriacaoAnamnese createUpdatedEntity(EntityManager em) {
        CriacaoAnamnese criacaoAnamnese = new CriacaoAnamnese().descricao(UPDATED_DESCRICAO).idMedico(UPDATED_ID_MEDICO);
        return criacaoAnamnese;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_criacao_anamnese__criacao").block();
            em.deleteAll(CriacaoAnamnese.class).block();
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
        criacaoAnamnese = createEntity(em);
    }

    @Test
    void createCriacaoAnamnese() throws Exception {
        int databaseSizeBeforeCreate = criacaoAnamneseRepository.findAll().collectList().block().size();
        // Create the CriacaoAnamnese
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoAnamnese))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the CriacaoAnamnese in the database
        List<CriacaoAnamnese> criacaoAnamneseList = criacaoAnamneseRepository.findAll().collectList().block();
        assertThat(criacaoAnamneseList).hasSize(databaseSizeBeforeCreate + 1);
        CriacaoAnamnese testCriacaoAnamnese = criacaoAnamneseList.get(criacaoAnamneseList.size() - 1);
        assertThat(testCriacaoAnamnese.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testCriacaoAnamnese.getIdMedico()).isEqualTo(DEFAULT_ID_MEDICO);
    }

    @Test
    void createCriacaoAnamneseWithExistingId() throws Exception {
        // Create the CriacaoAnamnese with an existing ID
        criacaoAnamnese.setId(1L);

        int databaseSizeBeforeCreate = criacaoAnamneseRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoAnamnese))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoAnamnese in the database
        List<CriacaoAnamnese> criacaoAnamneseList = criacaoAnamneseRepository.findAll().collectList().block();
        assertThat(criacaoAnamneseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCriacaoAnamnese() {
        // Initialize the database
        criacaoAnamneseRepository.save(criacaoAnamnese).block();

        // Get all the criacaoAnamneseList
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
            .value(hasItem(criacaoAnamnese.getId().intValue()))
            .jsonPath("$.[*].descricao")
            .value(hasItem(DEFAULT_DESCRICAO))
            .jsonPath("$.[*].idMedico")
            .value(hasItem(DEFAULT_ID_MEDICO.intValue()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCriacaoAnamneseWithEagerRelationshipsIsEnabled() {
        when(criacaoAnamneseRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(criacaoAnamneseRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCriacaoAnamneseWithEagerRelationshipsIsNotEnabled() {
        when(criacaoAnamneseRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(criacaoAnamneseRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getCriacaoAnamnese() {
        // Initialize the database
        criacaoAnamneseRepository.save(criacaoAnamnese).block();

        // Get the criacaoAnamnese
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, criacaoAnamnese.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(criacaoAnamnese.getId().intValue()))
            .jsonPath("$.descricao")
            .value(is(DEFAULT_DESCRICAO))
            .jsonPath("$.idMedico")
            .value(is(DEFAULT_ID_MEDICO.intValue()));
    }

    @Test
    void getNonExistingCriacaoAnamnese() {
        // Get the criacaoAnamnese
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCriacaoAnamnese() throws Exception {
        // Initialize the database
        criacaoAnamneseRepository.save(criacaoAnamnese).block();

        int databaseSizeBeforeUpdate = criacaoAnamneseRepository.findAll().collectList().block().size();

        // Update the criacaoAnamnese
        CriacaoAnamnese updatedCriacaoAnamnese = criacaoAnamneseRepository.findById(criacaoAnamnese.getId()).block();
        updatedCriacaoAnamnese.descricao(UPDATED_DESCRICAO).idMedico(UPDATED_ID_MEDICO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCriacaoAnamnese.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCriacaoAnamnese))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CriacaoAnamnese in the database
        List<CriacaoAnamnese> criacaoAnamneseList = criacaoAnamneseRepository.findAll().collectList().block();
        assertThat(criacaoAnamneseList).hasSize(databaseSizeBeforeUpdate);
        CriacaoAnamnese testCriacaoAnamnese = criacaoAnamneseList.get(criacaoAnamneseList.size() - 1);
        assertThat(testCriacaoAnamnese.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testCriacaoAnamnese.getIdMedico()).isEqualTo(UPDATED_ID_MEDICO);
    }

    @Test
    void putNonExistingCriacaoAnamnese() throws Exception {
        int databaseSizeBeforeUpdate = criacaoAnamneseRepository.findAll().collectList().block().size();
        criacaoAnamnese.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, criacaoAnamnese.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoAnamnese))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoAnamnese in the database
        List<CriacaoAnamnese> criacaoAnamneseList = criacaoAnamneseRepository.findAll().collectList().block();
        assertThat(criacaoAnamneseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCriacaoAnamnese() throws Exception {
        int databaseSizeBeforeUpdate = criacaoAnamneseRepository.findAll().collectList().block().size();
        criacaoAnamnese.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoAnamnese))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoAnamnese in the database
        List<CriacaoAnamnese> criacaoAnamneseList = criacaoAnamneseRepository.findAll().collectList().block();
        assertThat(criacaoAnamneseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCriacaoAnamnese() throws Exception {
        int databaseSizeBeforeUpdate = criacaoAnamneseRepository.findAll().collectList().block().size();
        criacaoAnamnese.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoAnamnese))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CriacaoAnamnese in the database
        List<CriacaoAnamnese> criacaoAnamneseList = criacaoAnamneseRepository.findAll().collectList().block();
        assertThat(criacaoAnamneseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCriacaoAnamneseWithPatch() throws Exception {
        // Initialize the database
        criacaoAnamneseRepository.save(criacaoAnamnese).block();

        int databaseSizeBeforeUpdate = criacaoAnamneseRepository.findAll().collectList().block().size();

        // Update the criacaoAnamnese using partial update
        CriacaoAnamnese partialUpdatedCriacaoAnamnese = new CriacaoAnamnese();
        partialUpdatedCriacaoAnamnese.setId(criacaoAnamnese.getId());

        partialUpdatedCriacaoAnamnese.descricao(UPDATED_DESCRICAO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCriacaoAnamnese.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCriacaoAnamnese))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CriacaoAnamnese in the database
        List<CriacaoAnamnese> criacaoAnamneseList = criacaoAnamneseRepository.findAll().collectList().block();
        assertThat(criacaoAnamneseList).hasSize(databaseSizeBeforeUpdate);
        CriacaoAnamnese testCriacaoAnamnese = criacaoAnamneseList.get(criacaoAnamneseList.size() - 1);
        assertThat(testCriacaoAnamnese.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testCriacaoAnamnese.getIdMedico()).isEqualTo(DEFAULT_ID_MEDICO);
    }

    @Test
    void fullUpdateCriacaoAnamneseWithPatch() throws Exception {
        // Initialize the database
        criacaoAnamneseRepository.save(criacaoAnamnese).block();

        int databaseSizeBeforeUpdate = criacaoAnamneseRepository.findAll().collectList().block().size();

        // Update the criacaoAnamnese using partial update
        CriacaoAnamnese partialUpdatedCriacaoAnamnese = new CriacaoAnamnese();
        partialUpdatedCriacaoAnamnese.setId(criacaoAnamnese.getId());

        partialUpdatedCriacaoAnamnese.descricao(UPDATED_DESCRICAO).idMedico(UPDATED_ID_MEDICO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCriacaoAnamnese.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCriacaoAnamnese))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CriacaoAnamnese in the database
        List<CriacaoAnamnese> criacaoAnamneseList = criacaoAnamneseRepository.findAll().collectList().block();
        assertThat(criacaoAnamneseList).hasSize(databaseSizeBeforeUpdate);
        CriacaoAnamnese testCriacaoAnamnese = criacaoAnamneseList.get(criacaoAnamneseList.size() - 1);
        assertThat(testCriacaoAnamnese.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testCriacaoAnamnese.getIdMedico()).isEqualTo(UPDATED_ID_MEDICO);
    }

    @Test
    void patchNonExistingCriacaoAnamnese() throws Exception {
        int databaseSizeBeforeUpdate = criacaoAnamneseRepository.findAll().collectList().block().size();
        criacaoAnamnese.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, criacaoAnamnese.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoAnamnese))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoAnamnese in the database
        List<CriacaoAnamnese> criacaoAnamneseList = criacaoAnamneseRepository.findAll().collectList().block();
        assertThat(criacaoAnamneseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCriacaoAnamnese() throws Exception {
        int databaseSizeBeforeUpdate = criacaoAnamneseRepository.findAll().collectList().block().size();
        criacaoAnamnese.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoAnamnese))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CriacaoAnamnese in the database
        List<CriacaoAnamnese> criacaoAnamneseList = criacaoAnamneseRepository.findAll().collectList().block();
        assertThat(criacaoAnamneseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCriacaoAnamnese() throws Exception {
        int databaseSizeBeforeUpdate = criacaoAnamneseRepository.findAll().collectList().block().size();
        criacaoAnamnese.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(criacaoAnamnese))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CriacaoAnamnese in the database
        List<CriacaoAnamnese> criacaoAnamneseList = criacaoAnamneseRepository.findAll().collectList().block();
        assertThat(criacaoAnamneseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCriacaoAnamnese() {
        // Initialize the database
        criacaoAnamneseRepository.save(criacaoAnamnese).block();

        int databaseSizeBeforeDelete = criacaoAnamneseRepository.findAll().collectList().block().size();

        // Delete the criacaoAnamnese
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, criacaoAnamnese.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<CriacaoAnamnese> criacaoAnamneseList = criacaoAnamneseRepository.findAll().collectList().block();
        assertThat(criacaoAnamneseList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
