package org.forsstudio.nbatestsuit.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.forsstudio.nbatestsuit.domain.GameAsserts.*;
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
import org.forsstudio.nbatestsuit.domain.Game;
import org.forsstudio.nbatestsuit.repository.EntityManager;
import org.forsstudio.nbatestsuit.repository.GameRepository;
import org.forsstudio.nbatestsuit.repository.search.GameSearchRepository;
import org.forsstudio.nbatestsuit.service.dto.GameDTO;
import org.forsstudio.nbatestsuit.service.mapper.GameMapper;
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
 * Integration tests for the {@link GameResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class GameResourceIT {

    private static final String ENTITY_API_URL = "/api/games";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/games/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameMapper gameMapper;

    @Autowired
    private GameSearchRepository gameSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Game game;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Game createEntity(EntityManager em) {
        Game game = new Game();
        return game;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Game createUpdatedEntity(EntityManager em) {
        Game game = new Game();
        return game;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Game.class).block();
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
        gameSearchRepository.deleteAll().block();
        assertThat(gameSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        game = createEntity(em);
    }

    @Test
    void createGame() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());
        // Create the Game
        GameDTO gameDTO = gameMapper.toDto(game);
        var returnedGameDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(gameDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(GameDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Game in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedGame = gameMapper.toEntity(returnedGameDTO);
        assertGameUpdatableFieldsEquals(returnedGame, getPersistedGame(returnedGame));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
    }

    @Test
    void createGameWithExistingId() throws Exception {
        // Create the Game with an existing ID
        game.setId(1L);
        GameDTO gameDTO = gameMapper.toDto(game);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(gameDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Game in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllGames() {
        // Initialize the database
        gameRepository.save(game).block();

        // Get all the gameList
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
            .value(hasItem(game.getId().intValue()));
    }

    @Test
    void getGame() {
        // Initialize the database
        gameRepository.save(game).block();

        // Get the game
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, game.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(game.getId().intValue()));
    }

    @Test
    void getNonExistingGame() {
        // Get the game
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingGame() throws Exception {
        // Initialize the database
        gameRepository.save(game).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        gameSearchRepository.save(game).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());

        // Update the game
        Game updatedGame = gameRepository.findById(game.getId()).block();
        GameDTO gameDTO = gameMapper.toDto(updatedGame);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, gameDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(gameDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Game in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGameToMatchAllProperties(updatedGame);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Game> gameSearchList = Streamable.of(gameSearchRepository.findAll().collectList().block()).toList();
                Game testGameSearch = gameSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertGameAllPropertiesEquals(testGameSearch, updatedGame);
                assertGameUpdatableFieldsEquals(testGameSearch, updatedGame);
            });
    }

    @Test
    void putNonExistingGame() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());
        game.setId(longCount.incrementAndGet());

        // Create the Game
        GameDTO gameDTO = gameMapper.toDto(game);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, gameDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(gameDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Game in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchGame() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());
        game.setId(longCount.incrementAndGet());

        // Create the Game
        GameDTO gameDTO = gameMapper.toDto(game);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(gameDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Game in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamGame() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());
        game.setId(longCount.incrementAndGet());

        // Create the Game
        GameDTO gameDTO = gameMapper.toDto(game);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(gameDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Game in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateGameWithPatch() throws Exception {
        // Initialize the database
        gameRepository.save(game).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the game using partial update
        Game partialUpdatedGame = new Game();
        partialUpdatedGame.setId(game.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedGame.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedGame))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Game in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGameUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedGame, game), getPersistedGame(game));
    }

    @Test
    void fullUpdateGameWithPatch() throws Exception {
        // Initialize the database
        gameRepository.save(game).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the game using partial update
        Game partialUpdatedGame = new Game();
        partialUpdatedGame.setId(game.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedGame.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedGame))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Game in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGameUpdatableFieldsEquals(partialUpdatedGame, getPersistedGame(partialUpdatedGame));
    }

    @Test
    void patchNonExistingGame() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());
        game.setId(longCount.incrementAndGet());

        // Create the Game
        GameDTO gameDTO = gameMapper.toDto(game);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, gameDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(gameDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Game in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchGame() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());
        game.setId(longCount.incrementAndGet());

        // Create the Game
        GameDTO gameDTO = gameMapper.toDto(game);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(gameDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Game in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamGame() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());
        game.setId(longCount.incrementAndGet());

        // Create the Game
        GameDTO gameDTO = gameMapper.toDto(game);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(gameDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Game in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteGame() {
        // Initialize the database
        gameRepository.save(game).block();
        gameRepository.save(game).block();
        gameSearchRepository.save(game).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the game
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, game.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(gameSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchGame() {
        // Initialize the database
        game = gameRepository.save(game).block();
        gameSearchRepository.save(game).block();

        // Search the game
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + game.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(game.getId().intValue()));
    }

    protected long getRepositoryCount() {
        return gameRepository.count().block();
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

    protected Game getPersistedGame(Game game) {
        return gameRepository.findById(game.getId()).block();
    }

    protected void assertPersistedGameToMatchAllProperties(Game expectedGame) {
        // Test fails because reactive api returns an empty object instead of null
        // assertGameAllPropertiesEquals(expectedGame, getPersistedGame(expectedGame));
        assertGameUpdatableFieldsEquals(expectedGame, getPersistedGame(expectedGame));
    }

    protected void assertPersistedGameToMatchUpdatableProperties(Game expectedGame) {
        // Test fails because reactive api returns an empty object instead of null
        // assertGameAllUpdatablePropertiesEquals(expectedGame, getPersistedGame(expectedGame));
        assertGameUpdatableFieldsEquals(expectedGame, getPersistedGame(expectedGame));
    }
}
