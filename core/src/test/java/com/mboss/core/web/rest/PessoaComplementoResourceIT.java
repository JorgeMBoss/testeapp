package com.mboss.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.mboss.core.IntegrationTest;
import com.mboss.core.domain.PessoaComplemento;
import com.mboss.core.repository.PessoaComplementoRepository;
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
 * Integration tests for the {@link PessoaComplementoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PessoaComplementoResourceIT {

    private static final Long DEFAULT_IE = 1L;
    private static final Long UPDATED_IE = 2L;

    private static final Long DEFAULT_IM = 1L;
    private static final Long UPDATED_IM = 2L;

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/pessoa-complementos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PessoaComplementoRepository pessoaComplementoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PessoaComplemento pessoaComplemento;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaComplemento createEntity(EntityManager em) {
        PessoaComplemento pessoaComplemento = new PessoaComplemento().ie(DEFAULT_IE).im(DEFAULT_IM).email(DEFAULT_EMAIL);
        return pessoaComplemento;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PessoaComplemento createUpdatedEntity(EntityManager em) {
        PessoaComplemento pessoaComplemento = new PessoaComplemento().ie(UPDATED_IE).im(UPDATED_IM).email(UPDATED_EMAIL);
        return pessoaComplemento;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PessoaComplemento.class).block();
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
        pessoaComplemento = createEntity(em);
    }

    @Test
    void createPessoaComplemento() throws Exception {
        int databaseSizeBeforeCreate = pessoaComplementoRepository.findAll().collectList().block().size();
        // Create the PessoaComplemento
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaComplemento))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the PessoaComplemento in the database
        List<PessoaComplemento> pessoaComplementoList = pessoaComplementoRepository.findAll().collectList().block();
        assertThat(pessoaComplementoList).hasSize(databaseSizeBeforeCreate + 1);
        PessoaComplemento testPessoaComplemento = pessoaComplementoList.get(pessoaComplementoList.size() - 1);
        assertThat(testPessoaComplemento.getIe()).isEqualTo(DEFAULT_IE);
        assertThat(testPessoaComplemento.getIm()).isEqualTo(DEFAULT_IM);
        assertThat(testPessoaComplemento.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    void createPessoaComplementoWithExistingId() throws Exception {
        // Create the PessoaComplemento with an existing ID
        pessoaComplemento.setId(1L);

        int databaseSizeBeforeCreate = pessoaComplementoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaComplemento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaComplemento in the database
        List<PessoaComplemento> pessoaComplementoList = pessoaComplementoRepository.findAll().collectList().block();
        assertThat(pessoaComplementoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPessoaComplementosAsStream() {
        // Initialize the database
        pessoaComplementoRepository.save(pessoaComplemento).block();

        List<PessoaComplemento> pessoaComplementoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PessoaComplemento.class)
            .getResponseBody()
            .filter(pessoaComplemento::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(pessoaComplementoList).isNotNull();
        assertThat(pessoaComplementoList).hasSize(1);
        PessoaComplemento testPessoaComplemento = pessoaComplementoList.get(0);
        assertThat(testPessoaComplemento.getIe()).isEqualTo(DEFAULT_IE);
        assertThat(testPessoaComplemento.getIm()).isEqualTo(DEFAULT_IM);
        assertThat(testPessoaComplemento.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    void getAllPessoaComplementos() {
        // Initialize the database
        pessoaComplementoRepository.save(pessoaComplemento).block();

        // Get all the pessoaComplementoList
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
            .value(hasItem(pessoaComplemento.getId().intValue()))
            .jsonPath("$.[*].ie")
            .value(hasItem(DEFAULT_IE.intValue()))
            .jsonPath("$.[*].im")
            .value(hasItem(DEFAULT_IM.intValue()))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL));
    }

    @Test
    void getPessoaComplemento() {
        // Initialize the database
        pessoaComplementoRepository.save(pessoaComplemento).block();

        // Get the pessoaComplemento
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pessoaComplemento.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pessoaComplemento.getId().intValue()))
            .jsonPath("$.ie")
            .value(is(DEFAULT_IE.intValue()))
            .jsonPath("$.im")
            .value(is(DEFAULT_IM.intValue()))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL));
    }

    @Test
    void getNonExistingPessoaComplemento() {
        // Get the pessoaComplemento
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPessoaComplemento() throws Exception {
        // Initialize the database
        pessoaComplementoRepository.save(pessoaComplemento).block();

        int databaseSizeBeforeUpdate = pessoaComplementoRepository.findAll().collectList().block().size();

        // Update the pessoaComplemento
        PessoaComplemento updatedPessoaComplemento = pessoaComplementoRepository.findById(pessoaComplemento.getId()).block();
        updatedPessoaComplemento.ie(UPDATED_IE).im(UPDATED_IM).email(UPDATED_EMAIL);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPessoaComplemento.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPessoaComplemento))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaComplemento in the database
        List<PessoaComplemento> pessoaComplementoList = pessoaComplementoRepository.findAll().collectList().block();
        assertThat(pessoaComplementoList).hasSize(databaseSizeBeforeUpdate);
        PessoaComplemento testPessoaComplemento = pessoaComplementoList.get(pessoaComplementoList.size() - 1);
        assertThat(testPessoaComplemento.getIe()).isEqualTo(UPDATED_IE);
        assertThat(testPessoaComplemento.getIm()).isEqualTo(UPDATED_IM);
        assertThat(testPessoaComplemento.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    void putNonExistingPessoaComplemento() throws Exception {
        int databaseSizeBeforeUpdate = pessoaComplementoRepository.findAll().collectList().block().size();
        pessoaComplemento.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pessoaComplemento.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaComplemento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaComplemento in the database
        List<PessoaComplemento> pessoaComplementoList = pessoaComplementoRepository.findAll().collectList().block();
        assertThat(pessoaComplementoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPessoaComplemento() throws Exception {
        int databaseSizeBeforeUpdate = pessoaComplementoRepository.findAll().collectList().block().size();
        pessoaComplemento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaComplemento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaComplemento in the database
        List<PessoaComplemento> pessoaComplementoList = pessoaComplementoRepository.findAll().collectList().block();
        assertThat(pessoaComplementoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPessoaComplemento() throws Exception {
        int databaseSizeBeforeUpdate = pessoaComplementoRepository.findAll().collectList().block().size();
        pessoaComplemento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaComplemento))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaComplemento in the database
        List<PessoaComplemento> pessoaComplementoList = pessoaComplementoRepository.findAll().collectList().block();
        assertThat(pessoaComplementoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePessoaComplementoWithPatch() throws Exception {
        // Initialize the database
        pessoaComplementoRepository.save(pessoaComplemento).block();

        int databaseSizeBeforeUpdate = pessoaComplementoRepository.findAll().collectList().block().size();

        // Update the pessoaComplemento using partial update
        PessoaComplemento partialUpdatedPessoaComplemento = new PessoaComplemento();
        partialUpdatedPessoaComplemento.setId(pessoaComplemento.getId());

        partialUpdatedPessoaComplemento.ie(UPDATED_IE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaComplemento.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaComplemento))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaComplemento in the database
        List<PessoaComplemento> pessoaComplementoList = pessoaComplementoRepository.findAll().collectList().block();
        assertThat(pessoaComplementoList).hasSize(databaseSizeBeforeUpdate);
        PessoaComplemento testPessoaComplemento = pessoaComplementoList.get(pessoaComplementoList.size() - 1);
        assertThat(testPessoaComplemento.getIe()).isEqualTo(UPDATED_IE);
        assertThat(testPessoaComplemento.getIm()).isEqualTo(DEFAULT_IM);
        assertThat(testPessoaComplemento.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    void fullUpdatePessoaComplementoWithPatch() throws Exception {
        // Initialize the database
        pessoaComplementoRepository.save(pessoaComplemento).block();

        int databaseSizeBeforeUpdate = pessoaComplementoRepository.findAll().collectList().block().size();

        // Update the pessoaComplemento using partial update
        PessoaComplemento partialUpdatedPessoaComplemento = new PessoaComplemento();
        partialUpdatedPessoaComplemento.setId(pessoaComplemento.getId());

        partialUpdatedPessoaComplemento.ie(UPDATED_IE).im(UPDATED_IM).email(UPDATED_EMAIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoaComplemento.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoaComplemento))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PessoaComplemento in the database
        List<PessoaComplemento> pessoaComplementoList = pessoaComplementoRepository.findAll().collectList().block();
        assertThat(pessoaComplementoList).hasSize(databaseSizeBeforeUpdate);
        PessoaComplemento testPessoaComplemento = pessoaComplementoList.get(pessoaComplementoList.size() - 1);
        assertThat(testPessoaComplemento.getIe()).isEqualTo(UPDATED_IE);
        assertThat(testPessoaComplemento.getIm()).isEqualTo(UPDATED_IM);
        assertThat(testPessoaComplemento.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    void patchNonExistingPessoaComplemento() throws Exception {
        int databaseSizeBeforeUpdate = pessoaComplementoRepository.findAll().collectList().block().size();
        pessoaComplemento.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pessoaComplemento.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaComplemento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaComplemento in the database
        List<PessoaComplemento> pessoaComplementoList = pessoaComplementoRepository.findAll().collectList().block();
        assertThat(pessoaComplementoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPessoaComplemento() throws Exception {
        int databaseSizeBeforeUpdate = pessoaComplementoRepository.findAll().collectList().block().size();
        pessoaComplemento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaComplemento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PessoaComplemento in the database
        List<PessoaComplemento> pessoaComplementoList = pessoaComplementoRepository.findAll().collectList().block();
        assertThat(pessoaComplementoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPessoaComplemento() throws Exception {
        int databaseSizeBeforeUpdate = pessoaComplementoRepository.findAll().collectList().block().size();
        pessoaComplemento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoaComplemento))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PessoaComplemento in the database
        List<PessoaComplemento> pessoaComplementoList = pessoaComplementoRepository.findAll().collectList().block();
        assertThat(pessoaComplementoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePessoaComplemento() {
        // Initialize the database
        pessoaComplementoRepository.save(pessoaComplemento).block();

        int databaseSizeBeforeDelete = pessoaComplementoRepository.findAll().collectList().block().size();

        // Delete the pessoaComplemento
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pessoaComplemento.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<PessoaComplemento> pessoaComplementoList = pessoaComplementoRepository.findAll().collectList().block();
        assertThat(pessoaComplementoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
