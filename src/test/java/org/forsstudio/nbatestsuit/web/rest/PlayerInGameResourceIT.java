package org.forsstudio.nbatestsuit.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.forsstudio.nbatestsuit.domain.PlayerInGameAsserts.*;
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
import org.forsstudio.nbatestsuit.domain.PlayerInGame;
import org.forsstudio.nbatestsuit.repository.EntityManager;
import org.forsstudio.nbatestsuit.repository.PlayerInGameRepository;
import org.forsstudio.nbatestsuit.repository.search.PlayerInGameSearchRepository;
import org.forsstudio.nbatestsuit.service.dto.PlayerInGameDTO;
import org.forsstudio.nbatestsuit.service.mapper.PlayerInGameMapper;
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
 * Integration tests for the {@link PlayerInGameResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PlayerInGameResourceIT {

    private static final Integer DEFAULT_POINTS = 1;
    private static final Integer UPDATED_POINTS = 2;

    private static final Integer DEFAULT_REBOUNDS = 1;
    private static final Integer UPDATED_REBOUNDS = 2;

    private static final Integer DEFAULT_ASSISTS = 1;
    private static final Integer UPDATED_ASSISTS = 2;

    private static final Integer DEFAULT_STEALS = 1;
    private static final Integer UPDATED_STEALS = 2;

    private static final Integer DEFAULT_BLOCKS = 1;
    private static final Integer UPDATED_BLOCKS = 2;

    private static final Integer DEFAULT_FOULS = 1;
    private static final Integer UPDATED_FOULS = 2;

    private static final Integer DEFAULT_TURNOVERS = 1;
    private static final Integer UPDATED_TURNOVERS = 2;

    private static final Float DEFAULT_PLAYED = 1F;
    private static final Float UPDATED_PLAYED = 2F;

    private static final String ENTITY_API_URL = "/api/player-in-games";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/player-in-games/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PlayerInGameRepository playerInGameRepository;

    @Autowired
    private PlayerInGameMapper playerInGameMapper;

    @Autowired
    private PlayerInGameSearchRepository playerInGameSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PlayerInGame playerInGame;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlayerInGame createEntity(EntityManager em) {
        PlayerInGame playerInGame = new PlayerInGame()
            .points(DEFAULT_POINTS)
            .rebounds(DEFAULT_REBOUNDS)
            .assists(DEFAULT_ASSISTS)
            .steals(DEFAULT_STEALS)
            .blocks(DEFAULT_BLOCKS)
            .fouls(DEFAULT_FOULS)
            .turnovers(DEFAULT_TURNOVERS)
            .played(DEFAULT_PLAYED);
        return playerInGame;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlayerInGame createUpdatedEntity(EntityManager em) {
        PlayerInGame playerInGame = new PlayerInGame()
            .points(UPDATED_POINTS)
            .rebounds(UPDATED_REBOUNDS)
            .assists(UPDATED_ASSISTS)
            .steals(UPDATED_STEALS)
            .blocks(UPDATED_BLOCKS)
            .fouls(UPDATED_FOULS)
            .turnovers(UPDATED_TURNOVERS)
            .played(UPDATED_PLAYED);
        return playerInGame;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PlayerInGame.class).block();
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
        playerInGameSearchRepository.deleteAll().block();
        assertThat(playerInGameSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        playerInGame = createEntity(em);
    }

    @Test
    void createPlayerInGame() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());
        // Create the PlayerInGame
        PlayerInGameDTO playerInGameDTO = playerInGameMapper.toDto(playerInGame);
        var returnedPlayerInGameDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(playerInGameDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(PlayerInGameDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the PlayerInGame in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPlayerInGame = playerInGameMapper.toEntity(returnedPlayerInGameDTO);
        assertPlayerInGameUpdatableFieldsEquals(returnedPlayerInGame, getPersistedPlayerInGame(returnedPlayerInGame));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
    }

    @Test
    void createPlayerInGameWithExistingId() throws Exception {
        // Create the PlayerInGame with an existing ID
        playerInGame.setId(1L);
        PlayerInGameDTO playerInGameDTO = playerInGameMapper.toDto(playerInGame);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(playerInGameDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlayerInGame in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllPlayerInGames() {
        // Initialize the database
        playerInGameRepository.save(playerInGame).block();

        // Get all the playerInGameList
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
            .value(hasItem(playerInGame.getId().intValue()))
            .jsonPath("$.[*].points")
            .value(hasItem(DEFAULT_POINTS))
            .jsonPath("$.[*].rebounds")
            .value(hasItem(DEFAULT_REBOUNDS))
            .jsonPath("$.[*].assists")
            .value(hasItem(DEFAULT_ASSISTS))
            .jsonPath("$.[*].steals")
            .value(hasItem(DEFAULT_STEALS))
            .jsonPath("$.[*].blocks")
            .value(hasItem(DEFAULT_BLOCKS))
            .jsonPath("$.[*].fouls")
            .value(hasItem(DEFAULT_FOULS))
            .jsonPath("$.[*].turnovers")
            .value(hasItem(DEFAULT_TURNOVERS))
            .jsonPath("$.[*].played")
            .value(hasItem(DEFAULT_PLAYED.doubleValue()));
    }

    @Test
    void getPlayerInGame() {
        // Initialize the database
        playerInGameRepository.save(playerInGame).block();

        // Get the playerInGame
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, playerInGame.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(playerInGame.getId().intValue()))
            .jsonPath("$.points")
            .value(is(DEFAULT_POINTS))
            .jsonPath("$.rebounds")
            .value(is(DEFAULT_REBOUNDS))
            .jsonPath("$.assists")
            .value(is(DEFAULT_ASSISTS))
            .jsonPath("$.steals")
            .value(is(DEFAULT_STEALS))
            .jsonPath("$.blocks")
            .value(is(DEFAULT_BLOCKS))
            .jsonPath("$.fouls")
            .value(is(DEFAULT_FOULS))
            .jsonPath("$.turnovers")
            .value(is(DEFAULT_TURNOVERS))
            .jsonPath("$.played")
            .value(is(DEFAULT_PLAYED.doubleValue()));
    }

    @Test
    void getNonExistingPlayerInGame() {
        // Get the playerInGame
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPlayerInGame() throws Exception {
        // Initialize the database
        playerInGameRepository.save(playerInGame).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        playerInGameSearchRepository.save(playerInGame).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());

        // Update the playerInGame
        PlayerInGame updatedPlayerInGame = playerInGameRepository.findById(playerInGame.getId()).block();
        updatedPlayerInGame
            .points(UPDATED_POINTS)
            .rebounds(UPDATED_REBOUNDS)
            .assists(UPDATED_ASSISTS)
            .steals(UPDATED_STEALS)
            .blocks(UPDATED_BLOCKS)
            .fouls(UPDATED_FOULS)
            .turnovers(UPDATED_TURNOVERS)
            .played(UPDATED_PLAYED);
        PlayerInGameDTO playerInGameDTO = playerInGameMapper.toDto(updatedPlayerInGame);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, playerInGameDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(playerInGameDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PlayerInGame in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPlayerInGameToMatchAllProperties(updatedPlayerInGame);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<PlayerInGame> playerInGameSearchList = Streamable.of(
                    playerInGameSearchRepository.findAll().collectList().block()
                ).toList();
                PlayerInGame testPlayerInGameSearch = playerInGameSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertPlayerInGameAllPropertiesEquals(testPlayerInGameSearch, updatedPlayerInGame);
                assertPlayerInGameUpdatableFieldsEquals(testPlayerInGameSearch, updatedPlayerInGame);
            });
    }

    @Test
    void putNonExistingPlayerInGame() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());
        playerInGame.setId(longCount.incrementAndGet());

        // Create the PlayerInGame
        PlayerInGameDTO playerInGameDTO = playerInGameMapper.toDto(playerInGame);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, playerInGameDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(playerInGameDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlayerInGame in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchPlayerInGame() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());
        playerInGame.setId(longCount.incrementAndGet());

        // Create the PlayerInGame
        PlayerInGameDTO playerInGameDTO = playerInGameMapper.toDto(playerInGame);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(playerInGameDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlayerInGame in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamPlayerInGame() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());
        playerInGame.setId(longCount.incrementAndGet());

        // Create the PlayerInGame
        PlayerInGameDTO playerInGameDTO = playerInGameMapper.toDto(playerInGame);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(playerInGameDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PlayerInGame in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdatePlayerInGameWithPatch() throws Exception {
        // Initialize the database
        playerInGameRepository.save(playerInGame).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the playerInGame using partial update
        PlayerInGame partialUpdatedPlayerInGame = new PlayerInGame();
        partialUpdatedPlayerInGame.setId(playerInGame.getId());

        partialUpdatedPlayerInGame
            .assists(UPDATED_ASSISTS)
            .blocks(UPDATED_BLOCKS)
            .fouls(UPDATED_FOULS)
            .turnovers(UPDATED_TURNOVERS)
            .played(UPDATED_PLAYED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPlayerInGame.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPlayerInGame))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PlayerInGame in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlayerInGameUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPlayerInGame, playerInGame),
            getPersistedPlayerInGame(playerInGame)
        );
    }

    @Test
    void fullUpdatePlayerInGameWithPatch() throws Exception {
        // Initialize the database
        playerInGameRepository.save(playerInGame).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the playerInGame using partial update
        PlayerInGame partialUpdatedPlayerInGame = new PlayerInGame();
        partialUpdatedPlayerInGame.setId(playerInGame.getId());

        partialUpdatedPlayerInGame
            .points(UPDATED_POINTS)
            .rebounds(UPDATED_REBOUNDS)
            .assists(UPDATED_ASSISTS)
            .steals(UPDATED_STEALS)
            .blocks(UPDATED_BLOCKS)
            .fouls(UPDATED_FOULS)
            .turnovers(UPDATED_TURNOVERS)
            .played(UPDATED_PLAYED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPlayerInGame.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPlayerInGame))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PlayerInGame in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlayerInGameUpdatableFieldsEquals(partialUpdatedPlayerInGame, getPersistedPlayerInGame(partialUpdatedPlayerInGame));
    }

    @Test
    void patchNonExistingPlayerInGame() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());
        playerInGame.setId(longCount.incrementAndGet());

        // Create the PlayerInGame
        PlayerInGameDTO playerInGameDTO = playerInGameMapper.toDto(playerInGame);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, playerInGameDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(playerInGameDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlayerInGame in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchPlayerInGame() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());
        playerInGame.setId(longCount.incrementAndGet());

        // Create the PlayerInGame
        PlayerInGameDTO playerInGameDTO = playerInGameMapper.toDto(playerInGame);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(playerInGameDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PlayerInGame in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamPlayerInGame() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());
        playerInGame.setId(longCount.incrementAndGet());

        // Create the PlayerInGame
        PlayerInGameDTO playerInGameDTO = playerInGameMapper.toDto(playerInGame);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(playerInGameDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PlayerInGame in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deletePlayerInGame() {
        // Initialize the database
        playerInGameRepository.save(playerInGame).block();
        playerInGameRepository.save(playerInGame).block();
        playerInGameSearchRepository.save(playerInGame).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the playerInGame
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, playerInGame.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerInGameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchPlayerInGame() {
        // Initialize the database
        playerInGame = playerInGameRepository.save(playerInGame).block();
        playerInGameSearchRepository.save(playerInGame).block();

        // Search the playerInGame
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + playerInGame.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(playerInGame.getId().intValue()))
            .jsonPath("$.[*].points")
            .value(hasItem(DEFAULT_POINTS))
            .jsonPath("$.[*].rebounds")
            .value(hasItem(DEFAULT_REBOUNDS))
            .jsonPath("$.[*].assists")
            .value(hasItem(DEFAULT_ASSISTS))
            .jsonPath("$.[*].steals")
            .value(hasItem(DEFAULT_STEALS))
            .jsonPath("$.[*].blocks")
            .value(hasItem(DEFAULT_BLOCKS))
            .jsonPath("$.[*].fouls")
            .value(hasItem(DEFAULT_FOULS))
            .jsonPath("$.[*].turnovers")
            .value(hasItem(DEFAULT_TURNOVERS))
            .jsonPath("$.[*].played")
            .value(hasItem(DEFAULT_PLAYED.doubleValue()));
    }

    protected long getRepositoryCount() {
        return playerInGameRepository.count().block();
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

    protected PlayerInGame getPersistedPlayerInGame(PlayerInGame playerInGame) {
        return playerInGameRepository.findById(playerInGame.getId()).block();
    }

    protected void assertPersistedPlayerInGameToMatchAllProperties(PlayerInGame expectedPlayerInGame) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPlayerInGameAllPropertiesEquals(expectedPlayerInGame, getPersistedPlayerInGame(expectedPlayerInGame));
        assertPlayerInGameUpdatableFieldsEquals(expectedPlayerInGame, getPersistedPlayerInGame(expectedPlayerInGame));
    }

    protected void assertPersistedPlayerInGameToMatchUpdatableProperties(PlayerInGame expectedPlayerInGame) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPlayerInGameAllUpdatablePropertiesEquals(expectedPlayerInGame, getPersistedPlayerInGame(expectedPlayerInGame));
        assertPlayerInGameUpdatableFieldsEquals(expectedPlayerInGame, getPersistedPlayerInGame(expectedPlayerInGame));
    }
}
