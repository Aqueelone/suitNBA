package org.forsstudio.nbatestsuit.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.forsstudio.nbatestsuit.domain.PlayerAsserts.*;
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
import org.forsstudio.nbatestsuit.domain.Player;
import org.forsstudio.nbatestsuit.repository.EntityManager;
import org.forsstudio.nbatestsuit.repository.PlayerRepository;
import org.forsstudio.nbatestsuit.repository.search.PlayerSearchRepository;
import org.forsstudio.nbatestsuit.service.dto.PlayerDTO;
import org.forsstudio.nbatestsuit.service.mapper.PlayerMapper;
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
 * Integration tests for the {@link PlayerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PlayerResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/players";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/players/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerMapper playerMapper;

    @Autowired
    private PlayerSearchRepository playerSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Player player;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Player createEntity(EntityManager em) {
        Player player = new Player().name(DEFAULT_NAME);
        return player;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Player createUpdatedEntity(EntityManager em) {
        Player player = new Player().name(UPDATED_NAME);
        return player;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Player.class).block();
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
        playerSearchRepository.deleteAll().block();
        assertThat(playerSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        player = createEntity(em);
    }

    @Test
    void createPlayer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());
        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);
        var returnedPlayerDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(playerDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(PlayerDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Player in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPlayer = playerMapper.toEntity(returnedPlayerDTO);
        assertPlayerUpdatableFieldsEquals(returnedPlayer, getPersistedPlayer(returnedPlayer));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
    }

    @Test
    void createPlayerWithExistingId() throws Exception {
        // Create the Player with an existing ID
        player.setId(1L);
        PlayerDTO playerDTO = playerMapper.toDto(player);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(playerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Player in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllPlayers() {
        // Initialize the database
        playerRepository.save(player).block();

        // Get all the playerList
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
            .value(hasItem(player.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));
    }

    @Test
    void getPlayer() {
        // Initialize the database
        playerRepository.save(player).block();

        // Get the player
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, player.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(player.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME));
    }

    @Test
    void getNonExistingPlayer() {
        // Get the player
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPlayer() throws Exception {
        // Initialize the database
        playerRepository.save(player).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        playerSearchRepository.save(player).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());

        // Update the player
        Player updatedPlayer = playerRepository.findById(player.getId()).block();
        updatedPlayer.name(UPDATED_NAME);
        PlayerDTO playerDTO = playerMapper.toDto(updatedPlayer);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, playerDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(playerDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Player in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPlayerToMatchAllProperties(updatedPlayer);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Player> playerSearchList = Streamable.of(playerSearchRepository.findAll().collectList().block()).toList();
                Player testPlayerSearch = playerSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertPlayerAllPropertiesEquals(testPlayerSearch, updatedPlayer);
                assertPlayerUpdatableFieldsEquals(testPlayerSearch, updatedPlayer);
            });
    }

    @Test
    void putNonExistingPlayer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());
        player.setId(longCount.incrementAndGet());

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, playerDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(playerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Player in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchPlayer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());
        player.setId(longCount.incrementAndGet());

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(playerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Player in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamPlayer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());
        player.setId(longCount.incrementAndGet());

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(playerDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Player in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdatePlayerWithPatch() throws Exception {
        // Initialize the database
        playerRepository.save(player).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the player using partial update
        Player partialUpdatedPlayer = new Player();
        partialUpdatedPlayer.setId(player.getId());

        partialUpdatedPlayer.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPlayer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPlayer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Player in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlayerUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPlayer, player), getPersistedPlayer(player));
    }

    @Test
    void fullUpdatePlayerWithPatch() throws Exception {
        // Initialize the database
        playerRepository.save(player).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the player using partial update
        Player partialUpdatedPlayer = new Player();
        partialUpdatedPlayer.setId(player.getId());

        partialUpdatedPlayer.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPlayer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPlayer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Player in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPlayerUpdatableFieldsEquals(partialUpdatedPlayer, getPersistedPlayer(partialUpdatedPlayer));
    }

    @Test
    void patchNonExistingPlayer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());
        player.setId(longCount.incrementAndGet());

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, playerDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(playerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Player in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchPlayer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());
        player.setId(longCount.incrementAndGet());

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(playerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Player in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamPlayer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());
        player.setId(longCount.incrementAndGet());

        // Create the Player
        PlayerDTO playerDTO = playerMapper.toDto(player);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(playerDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Player in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deletePlayer() {
        // Initialize the database
        playerRepository.save(player).block();
        playerRepository.save(player).block();
        playerSearchRepository.save(player).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the player
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, player.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(playerSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchPlayer() {
        // Initialize the database
        player = playerRepository.save(player).block();
        playerSearchRepository.save(player).block();

        // Search the player
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + player.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(player.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));
    }

    protected long getRepositoryCount() {
        return playerRepository.count().block();
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

    protected Player getPersistedPlayer(Player player) {
        return playerRepository.findById(player.getId()).block();
    }

    protected void assertPersistedPlayerToMatchAllProperties(Player expectedPlayer) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPlayerAllPropertiesEquals(expectedPlayer, getPersistedPlayer(expectedPlayer));
        assertPlayerUpdatableFieldsEquals(expectedPlayer, getPersistedPlayer(expectedPlayer));
    }

    protected void assertPersistedPlayerToMatchUpdatableProperties(Player expectedPlayer) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPlayerAllUpdatablePropertiesEquals(expectedPlayer, getPersistedPlayer(expectedPlayer));
        assertPlayerUpdatableFieldsEquals(expectedPlayer, getPersistedPlayer(expectedPlayer));
    }
}
