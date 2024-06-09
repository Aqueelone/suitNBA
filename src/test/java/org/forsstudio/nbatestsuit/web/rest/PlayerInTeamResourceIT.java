package org.forsstudio.nbatestsuit.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.forsstudio.nbatestsuit.domain.PlayerInTeamAsserts.*;
import static org.forsstudio.nbatestsuit.web.rest.TestUtil.createUpdateProxyForBean;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.forsstudio.nbatestsuit.IntegrationTest;
import org.forsstudio.nbatestsuit.domain.PlayerInTeam;
import org.forsstudio.nbatestsuit.repository.EntityManager;
import org.forsstudio.nbatestsuit.repository.PlayerInTeamRepository;
import org.forsstudio.nbatestsuit.repository.search.PlayerInTeamSearchRepository;
import org.forsstudio.nbatestsuit.service.dto.PlayerInTeamDTO;
import org.forsstudio.nbatestsuit.service.mapper.PlayerInTeamMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link PlayerInTeamResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PlayerInTeamResourceIT {

    private static final String ENTITY_API_URL = "/api/player-in-teams";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/player-in-teams/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PlayerInTeamRepository playerInTeamRepository;

    @Autowired
    private PlayerInTeamMapper playerInTeamMapper;

    @Autowired
    private PlayerInTeamSearchRepository playerInTeamSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PlayerInTeam playerInTeam;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlayerInTeam createEntity(EntityManager em) {
        PlayerInTeam playerInTeam = new PlayerInTeam();
        return playerInTeam;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlayerInTeam createUpdatedEntity(EntityManager em) {
        PlayerInTeam playerInTeam = new PlayerInTeam();
        return playerInTeam;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PlayerInTeam.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        playerInTeamSearchRepository.deleteAll().block();
        assertThat(playerInTeamSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        playerInTeam = createEntity(em);
    }

    @Test
    void createPlayerInTeam() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());
        // Create the PlayerInTeam
        PlayerInTeamDTO playerInTeamDTO = playerInTeamMapper.toDto(playerInTeam);
        var returnedPlayerInTeamDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(playerInTeamDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(PlayerInTeamDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the PlayerInTeam in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPlayerInTeam = playerInTeamMapper.toEntity(returnedPlayerInTeamDTO);
        assertPlayerInTeamUpdatableFieldsEquals(returnedPlayerInTeam, getPersistedPlayerInTeam(returnedPlayerInTeam));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
    }

    @Test
    void createPlayerInTeamWithExistingId() throws Exception {
        // Create the PlayerInTeam with an existing ID
        playerInTeam.setId(1L);
        PlayerInTeamDTO playerInTeamDTO = playerInTeamMapper.toDto(playerInTeam);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(playerInTeamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlayerInTeam in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllPlayerInTeams() {
        // Initialize the database
        playerInTeamRepository.save(playerInTeam).block();

        // Get all the playerInTeamList
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
            .value(hasItem(playerInTeam.getId().intValue()));
    }

    @Test
    void getPlayerInTeam() {
        // Initialize the database
        playerInTeamRepository.save(playerInTeam).block();

        // Get the playerInTeam
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, playerInTeam.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(playerInTeam.getId().intValue()));
    }

    @Test
    void getNonExistingPlayerInTeam() {
        // Get the playerInTeam
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPlayerInTeam() throws Exception {
        // Initialize the database
        playerInTeamRepository.save(playerInTeam).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        playerInTeamSearchRepository.save(playerInTeam).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());

        // Update the playerInTeam
        PlayerInTeam updatedPlayerInTeam = playerInTeamRepository.findById(playerInTeam.getId()).block();
        PlayerInTeamDTO playerInTeamDTO = playerInTeamMapper.toDto(updatedPlayerInTeam);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, playerInTeamDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(playerInTeamDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PlayerInTeam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPlayerInTeamToMatchAllProperties(updatedPlayerInTeam);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<PlayerInTeam> playerInTeamSearchList = Streamable.of(
                    playerInTeamSearchRepository.findAll().collectList().block()
                ).toList();
                PlayerInTeam testPlayerInTeamSearch = playerInTeamSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertPlayerInTeamAllPropertiesEquals(testPlayerInTeamSearch, updatedPlayerInTeam);
                assertPlayerInTeamUpdatableFieldsEquals(testPlayerInTeamSearch, updatedPlayerInTeam);
            });
    }

    @Test
    void putNonExistingPlayerInTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());
        playerInTeam.setId(longCount.incrementAndGet());

        // Create the PlayerInTeam
        PlayerInTeamDTO playerInTeamDTO = playerInTeamMapper.toDto(playerInTeam);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, playerInTeamDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(playerInTeamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlayerInTeam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchPlayerInTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());
        playerInTeam.setId(longCount.incrementAndGet());

        // Create the PlayerInTeam
        PlayerInTeamDTO playerInTeamDTO = playerInTeamMapper.toDto(playerInTeam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(playerInTeamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlayerInTeam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamPlayerInTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());
        playerInTeam.setId(longCount.incrementAndGet());

        // Create the PlayerInTeam
        PlayerInTeamDTO playerInTeamDTO = playerInTeamMapper.toDto(playerInTeam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(playerInTeamDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PlayerInTeam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdatePlayerInTeamWithPatch() throws Exception {
        // Initialize the database
        playerInTeamRepository.save(playerInTeam).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the playerInTeam using partial update
        PlayerInTeam partialUpdatedPlayerInTeam = new PlayerInTeam();
        partialUpdatedPlayerInTeam.setId(playerInTeam.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPlayerInTeam.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPlayerInTeam))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PlayerInTeam in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlayerInTeamUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPlayerInTeam, playerInTeam),
            getPersistedPlayerInTeam(playerInTeam)
        );
    }

    @Test
    void fullUpdatePlayerInTeamWithPatch() throws Exception {
        // Initialize the database
        playerInTeamRepository.save(playerInTeam).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the playerInTeam using partial update
        PlayerInTeam partialUpdatedPlayerInTeam = new PlayerInTeam();
        partialUpdatedPlayerInTeam.setId(playerInTeam.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPlayerInTeam.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPlayerInTeam))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PlayerInTeam in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlayerInTeamUpdatableFieldsEquals(partialUpdatedPlayerInTeam, getPersistedPlayerInTeam(partialUpdatedPlayerInTeam));
    }

    @Test
    void patchNonExistingPlayerInTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());
        playerInTeam.setId(longCount.incrementAndGet());

        // Create the PlayerInTeam
        PlayerInTeamDTO playerInTeamDTO = playerInTeamMapper.toDto(playerInTeam);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, playerInTeamDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(playerInTeamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlayerInTeam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchPlayerInTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());
        playerInTeam.setId(longCount.incrementAndGet());

        // Create the PlayerInTeam
        PlayerInTeamDTO playerInTeamDTO = playerInTeamMapper.toDto(playerInTeam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(playerInTeamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlayerInTeam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamPlayerInTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());
        playerInTeam.setId(longCount.incrementAndGet());

        // Create the PlayerInTeam
        PlayerInTeamDTO playerInTeamDTO = playerInTeamMapper.toDto(playerInTeam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(playerInTeamDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PlayerInTeam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deletePlayerInTeam() {
        // Initialize the database
        playerInTeamRepository.save(playerInTeam).block();
        playerInTeamRepository.save(playerInTeam).block();
        playerInTeamSearchRepository.save(playerInTeam).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the playerInTeam
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, playerInTeam.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInTeamSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchPlayerInTeam() {
        // Initialize the database
        playerInTeam = playerInTeamRepository.save(playerInTeam).block();
        playerInTeamSearchRepository.save(playerInTeam).block();

        // Search the playerInTeam
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + playerInTeam.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(playerInTeam.getId().intValue()));
    }

    protected long getRepositoryCount() {
        return playerInTeamRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected PlayerInTeam getPersistedPlayerInTeam(PlayerInTeam playerInTeam) {
        return playerInTeamRepository.findById(playerInTeam.getId()).block();
    }

    protected void assertPersistedPlayerInTeamToMatchAllProperties(PlayerInTeam expectedPlayerInTeam) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPlayerInTeamAllPropertiesEquals(expectedPlayerInTeam, getPersistedPlayerInTeam(expectedPlayerInTeam));
        assertPlayerInTeamUpdatableFieldsEquals(expectedPlayerInTeam, getPersistedPlayerInTeam(expectedPlayerInTeam));
    }

    protected void assertPersistedPlayerInTeamToMatchUpdatableProperties(PlayerInTeam expectedPlayerInTeam) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPlayerInTeamAllUpdatablePropertiesEquals(expectedPlayerInTeam, getPersistedPlayerInTeam(expectedPlayerInTeam));
        assertPlayerInTeamUpdatableFieldsEquals(expectedPlayerInTeam, getPersistedPlayerInTeam(expectedPlayerInTeam));
    }
}
