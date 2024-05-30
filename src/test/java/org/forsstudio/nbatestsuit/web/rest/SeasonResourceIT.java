package org.forsstudio.nbatestsuit.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.forsstudio.nbatestsuit.domain.SeasonAsserts.*;
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
import org.forsstudio.nbatestsuit.domain.Season;
import org.forsstudio.nbatestsuit.repository.EntityManager;
import org.forsstudio.nbatestsuit.repository.SeasonRepository;
import org.forsstudio.nbatestsuit.repository.search.SeasonSearchRepository;
import org.forsstudio.nbatestsuit.service.dto.SeasonDTO;
import org.forsstudio.nbatestsuit.service.mapper.SeasonMapper;
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
 * Integration tests for the {@link SeasonResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SeasonResourceIT {

    private static final String DEFAULT_SEASON_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SEASON_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/seasons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/seasons/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SeasonRepository seasonRepository;

    @Autowired
    private SeasonMapper seasonMapper;

    @Autowired
    private SeasonSearchRepository seasonSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Season season;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Season createEntity(EntityManager em) {
        Season season = new Season().seasonName(DEFAULT_SEASON_NAME);
        return season;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Season createUpdatedEntity(EntityManager em) {
        Season season = new Season().seasonName(UPDATED_SEASON_NAME);
        return season;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Season.class).block();
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
        seasonSearchRepository.deleteAll().block();
        assertThat(seasonSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        season = createEntity(em);
    }

    @Test
    void createSeason() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());
        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);
        var returnedSeasonDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(seasonDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SeasonDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Season in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSeason = seasonMapper.toEntity(returnedSeasonDTO);
        assertSeasonUpdatableFieldsEquals(returnedSeason, getPersistedSeason(returnedSeason));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
    }

    @Test
    void createSeasonWithExistingId() throws Exception {
        // Create the Season with an existing ID
        season.setId(1L);
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(seasonDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Season in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllSeasons() {
        // Initialize the database
        seasonRepository.save(season).block();

        // Get all the seasonList
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
            .value(hasItem(season.getId().intValue()))
            .jsonPath("$.[*].seasonName")
            .value(hasItem(DEFAULT_SEASON_NAME));
    }

    @Test
    void getSeason() {
        // Initialize the database
        seasonRepository.save(season).block();

        // Get the season
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, season.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(season.getId().intValue()))
            .jsonPath("$.seasonName")
            .value(is(DEFAULT_SEASON_NAME));
    }

    @Test
    void getNonExistingSeason() {
        // Get the season
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSeason() throws Exception {
        // Initialize the database
        seasonRepository.save(season).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        seasonSearchRepository.save(season).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());

        // Update the season
        Season updatedSeason = seasonRepository.findById(season.getId()).block();
        updatedSeason.seasonName(UPDATED_SEASON_NAME);
        SeasonDTO seasonDTO = seasonMapper.toDto(updatedSeason);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, seasonDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(seasonDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Season in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSeasonToMatchAllProperties(updatedSeason);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Season> seasonSearchList = Streamable.of(seasonSearchRepository.findAll().collectList().block()).toList();
                Season testSeasonSearch = seasonSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertSeasonAllPropertiesEquals(testSeasonSearch, updatedSeason);
                assertSeasonUpdatableFieldsEquals(testSeasonSearch, updatedSeason);
            });
    }

    @Test
    void putNonExistingSeason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());
        season.setId(longCount.incrementAndGet());

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, seasonDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(seasonDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Season in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchSeason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());
        season.setId(longCount.incrementAndGet());

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(seasonDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Season in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamSeason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());
        season.setId(longCount.incrementAndGet());

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(seasonDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Season in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateSeasonWithPatch() throws Exception {
        // Initialize the database
        seasonRepository.save(season).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the season using partial update
        Season partialUpdatedSeason = new Season();
        partialUpdatedSeason.setId(season.getId());

        partialUpdatedSeason.seasonName(UPDATED_SEASON_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSeason.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSeason))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Season in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSeasonUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSeason, season), getPersistedSeason(season));
    }

    @Test
    void fullUpdateSeasonWithPatch() throws Exception {
        // Initialize the database
        seasonRepository.save(season).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the season using partial update
        Season partialUpdatedSeason = new Season();
        partialUpdatedSeason.setId(season.getId());

        partialUpdatedSeason.seasonName(UPDATED_SEASON_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSeason.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSeason))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Season in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSeasonUpdatableFieldsEquals(partialUpdatedSeason, getPersistedSeason(partialUpdatedSeason));
    }

    @Test
    void patchNonExistingSeason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());
        season.setId(longCount.incrementAndGet());

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, seasonDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(seasonDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Season in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchSeason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());
        season.setId(longCount.incrementAndGet());

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(seasonDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Season in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamSeason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());
        season.setId(longCount.incrementAndGet());

        // Create the Season
        SeasonDTO seasonDTO = seasonMapper.toDto(season);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(seasonDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Season in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteSeason() {
        // Initialize the database
        seasonRepository.save(season).block();
        seasonRepository.save(season).block();
        seasonSearchRepository.save(season).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the season
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, season.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(seasonSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchSeason() {
        // Initialize the database
        season = seasonRepository.save(season).block();
        seasonSearchRepository.save(season).block();

        // Search the season
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + season.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(season.getId().intValue()))
            .jsonPath("$.[*].seasonName")
            .value(hasItem(DEFAULT_SEASON_NAME));
    }

    protected long getRepositoryCount() {
        return seasonRepository.count().block();
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

    protected Season getPersistedSeason(Season season) {
        return seasonRepository.findById(season.getId()).block();
    }

    protected void assertPersistedSeasonToMatchAllProperties(Season expectedSeason) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSeasonAllPropertiesEquals(expectedSeason, getPersistedSeason(expectedSeason));
        assertSeasonUpdatableFieldsEquals(expectedSeason, getPersistedSeason(expectedSeason));
    }

    protected void assertPersistedSeasonToMatchUpdatableProperties(Season expectedSeason) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSeasonAllUpdatablePropertiesEquals(expectedSeason, getPersistedSeason(expectedSeason));
        assertSeasonUpdatableFieldsEquals(expectedSeason, getPersistedSeason(expectedSeason));
    }
}
