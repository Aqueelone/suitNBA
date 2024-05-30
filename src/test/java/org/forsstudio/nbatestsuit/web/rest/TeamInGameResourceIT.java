package org.forsstudio.nbatestsuit.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.forsstudio.nbatestsuit.domain.TeamInGameAsserts.*;
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
import org.forsstudio.nbatestsuit.domain.TeamInGame;
import org.forsstudio.nbatestsuit.repository.EntityManager;
import org.forsstudio.nbatestsuit.repository.TeamInGameRepository;
import org.forsstudio.nbatestsuit.repository.search.TeamInGameSearchRepository;
import org.forsstudio.nbatestsuit.service.dto.TeamInGameDTO;
import org.forsstudio.nbatestsuit.service.mapper.TeamInGameMapper;
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
 * Integration tests for the {@link TeamInGameResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TeamInGameResourceIT {

    private static final String ENTITY_API_URL = "/api/team-in-games";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/team-in-games/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TeamInGameRepository teamInGameRepository;

    @Autowired
    private TeamInGameMapper teamInGameMapper;

    @Autowired
    private TeamInGameSearchRepository teamInGameSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TeamInGame teamInGame;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamInGame createEntity(EntityManager em) {
        TeamInGame teamInGame = new TeamInGame();
        return teamInGame;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamInGame createUpdatedEntity(EntityManager em) {
        TeamInGame teamInGame = new TeamInGame();
        return teamInGame;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TeamInGame.class).block();
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
        teamInGameSearchRepository.deleteAll().block();
        assertThat(teamInGameSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        teamInGame = createEntity(em);
    }

    @Test
    void createTeamInGame() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());
        // Create the TeamInGame
        TeamInGameDTO teamInGameDTO = teamInGameMapper.toDto(teamInGame);
        var returnedTeamInGameDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamInGameDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TeamInGameDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TeamInGame in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTeamInGame = teamInGameMapper.toEntity(returnedTeamInGameDTO);
        assertTeamInGameUpdatableFieldsEquals(returnedTeamInGame, getPersistedTeamInGame(returnedTeamInGame));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
    }

    @Test
    void createTeamInGameWithExistingId() throws Exception {
        // Create the TeamInGame with an existing ID
        teamInGame.setId(1L);
        TeamInGameDTO teamInGameDTO = teamInGameMapper.toDto(teamInGame);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamInGameDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TeamInGame in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTeamInGames() {
        // Initialize the database
        teamInGameRepository.save(teamInGame).block();

        // Get all the teamInGameList
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
            .value(hasItem(teamInGame.getId().intValue()));
    }

    @Test
    void getTeamInGame() {
        // Initialize the database
        teamInGameRepository.save(teamInGame).block();

        // Get the teamInGame
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, teamInGame.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(teamInGame.getId().intValue()));
    }

    @Test
    void getNonExistingTeamInGame() {
        // Get the teamInGame
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTeamInGame() throws Exception {
        // Initialize the database
        teamInGameRepository.save(teamInGame).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamInGameSearchRepository.save(teamInGame).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());

        // Update the teamInGame
        TeamInGame updatedTeamInGame = teamInGameRepository.findById(teamInGame.getId()).block();
        TeamInGameDTO teamInGameDTO = teamInGameMapper.toDto(updatedTeamInGame);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, teamInGameDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamInGameDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TeamInGame in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTeamInGameToMatchAllProperties(updatedTeamInGame);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TeamInGame> teamInGameSearchList = Streamable.of(teamInGameSearchRepository.findAll().collectList().block()).toList();
                TeamInGame testTeamInGameSearch = teamInGameSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTeamInGameAllPropertiesEquals(testTeamInGameSearch, updatedTeamInGame);
                assertTeamInGameUpdatableFieldsEquals(testTeamInGameSearch, updatedTeamInGame);
            });
    }

    @Test
    void putNonExistingTeamInGame() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());
        teamInGame.setId(longCount.incrementAndGet());

        // Create the TeamInGame
        TeamInGameDTO teamInGameDTO = teamInGameMapper.toDto(teamInGame);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, teamInGameDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamInGameDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TeamInGame in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTeamInGame() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());
        teamInGame.setId(longCount.incrementAndGet());

        // Create the TeamInGame
        TeamInGameDTO teamInGameDTO = teamInGameMapper.toDto(teamInGame);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamInGameDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TeamInGame in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTeamInGame() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());
        teamInGame.setId(longCount.incrementAndGet());

        // Create the TeamInGame
        TeamInGameDTO teamInGameDTO = teamInGameMapper.toDto(teamInGame);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamInGameDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TeamInGame in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTeamInGameWithPatch() throws Exception {
        // Initialize the database
        teamInGameRepository.save(teamInGame).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teamInGame using partial update
        TeamInGame partialUpdatedTeamInGame = new TeamInGame();
        partialUpdatedTeamInGame.setId(teamInGame.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTeamInGame.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTeamInGame))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TeamInGame in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeamInGameUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTeamInGame, teamInGame),
            getPersistedTeamInGame(teamInGame)
        );
    }

    @Test
    void fullUpdateTeamInGameWithPatch() throws Exception {
        // Initialize the database
        teamInGameRepository.save(teamInGame).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teamInGame using partial update
        TeamInGame partialUpdatedTeamInGame = new TeamInGame();
        partialUpdatedTeamInGame.setId(teamInGame.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTeamInGame.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTeamInGame))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TeamInGame in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeamInGameUpdatableFieldsEquals(partialUpdatedTeamInGame, getPersistedTeamInGame(partialUpdatedTeamInGame));
    }

    @Test
    void patchNonExistingTeamInGame() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());
        teamInGame.setId(longCount.incrementAndGet());

        // Create the TeamInGame
        TeamInGameDTO teamInGameDTO = teamInGameMapper.toDto(teamInGame);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, teamInGameDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(teamInGameDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TeamInGame in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTeamInGame() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());
        teamInGame.setId(longCount.incrementAndGet());

        // Create the TeamInGame
        TeamInGameDTO teamInGameDTO = teamInGameMapper.toDto(teamInGame);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(teamInGameDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TeamInGame in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTeamInGame() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());
        teamInGame.setId(longCount.incrementAndGet());

        // Create the TeamInGame
        TeamInGameDTO teamInGameDTO = teamInGameMapper.toDto(teamInGame);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(teamInGameDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TeamInGame in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTeamInGame() {
        // Initialize the database
        teamInGameRepository.save(teamInGame).block();
        teamInGameRepository.save(teamInGame).block();
        teamInGameSearchRepository.save(teamInGame).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the teamInGame
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, teamInGame.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInGameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTeamInGame() {
        // Initialize the database
        teamInGame = teamInGameRepository.save(teamInGame).block();
        teamInGameSearchRepository.save(teamInGame).block();

        // Search the teamInGame
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + teamInGame.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(teamInGame.getId().intValue()));
    }

    protected long getRepositoryCount() {
        return teamInGameRepository.count().block();
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

    protected TeamInGame getPersistedTeamInGame(TeamInGame teamInGame) {
        return teamInGameRepository.findById(teamInGame.getId()).block();
    }

    protected void assertPersistedTeamInGameToMatchAllProperties(TeamInGame expectedTeamInGame) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTeamInGameAllPropertiesEquals(expectedTeamInGame, getPersistedTeamInGame(expectedTeamInGame));
        assertTeamInGameUpdatableFieldsEquals(expectedTeamInGame, getPersistedTeamInGame(expectedTeamInGame));
    }

    protected void assertPersistedTeamInGameToMatchUpdatableProperties(TeamInGame expectedTeamInGame) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTeamInGameAllUpdatablePropertiesEquals(expectedTeamInGame, getPersistedTeamInGame(expectedTeamInGame));
        assertTeamInGameUpdatableFieldsEquals(expectedTeamInGame, getPersistedTeamInGame(expectedTeamInGame));
    }
}
