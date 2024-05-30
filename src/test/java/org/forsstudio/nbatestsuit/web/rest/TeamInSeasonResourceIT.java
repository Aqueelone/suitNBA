package org.forsstudio.nbatestsuit.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.forsstudio.nbatestsuit.domain.TeamInSeasonAsserts.*;
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
import org.forsstudio.nbatestsuit.domain.TeamInSeason;
import org.forsstudio.nbatestsuit.repository.EntityManager;
import org.forsstudio.nbatestsuit.repository.TeamInSeasonRepository;
import org.forsstudio.nbatestsuit.repository.search.TeamInSeasonSearchRepository;
import org.forsstudio.nbatestsuit.service.dto.TeamInSeasonDTO;
import org.forsstudio.nbatestsuit.service.mapper.TeamInSeasonMapper;
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
 * Integration tests for the {@link TeamInSeasonResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TeamInSeasonResourceIT {

    private static final String ENTITY_API_URL = "/api/team-in-seasons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/team-in-seasons/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TeamInSeasonRepository teamInSeasonRepository;

    @Autowired
    private TeamInSeasonMapper teamInSeasonMapper;

    @Autowired
    private TeamInSeasonSearchRepository teamInSeasonSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TeamInSeason teamInSeason;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamInSeason createEntity(EntityManager em) {
        TeamInSeason teamInSeason = new TeamInSeason();
        return teamInSeason;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamInSeason createUpdatedEntity(EntityManager em) {
        TeamInSeason teamInSeason = new TeamInSeason();
        return teamInSeason;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TeamInSeason.class).block();
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
        teamInSeasonSearchRepository.deleteAll().block();
        assertThat(teamInSeasonSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        teamInSeason = createEntity(em);
    }

    @Test
    void createTeamInSeason() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());
        // Create the TeamInSeason
        TeamInSeasonDTO teamInSeasonDTO = teamInSeasonMapper.toDto(teamInSeason);
        var returnedTeamInSeasonDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamInSeasonDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TeamInSeasonDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TeamInSeason in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTeamInSeason = teamInSeasonMapper.toEntity(returnedTeamInSeasonDTO);
        assertTeamInSeasonUpdatableFieldsEquals(returnedTeamInSeason, getPersistedTeamInSeason(returnedTeamInSeason));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
    }

    @Test
    void createTeamInSeasonWithExistingId() throws Exception {
        // Create the TeamInSeason with an existing ID
        teamInSeason.setId(1L);
        TeamInSeasonDTO teamInSeasonDTO = teamInSeasonMapper.toDto(teamInSeason);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamInSeasonDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TeamInSeason in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTeamInSeasons() {
        // Initialize the database
        teamInSeasonRepository.save(teamInSeason).block();

        // Get all the teamInSeasonList
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
            .value(hasItem(teamInSeason.getId().intValue()));
    }

    @Test
    void getTeamInSeason() {
        // Initialize the database
        teamInSeasonRepository.save(teamInSeason).block();

        // Get the teamInSeason
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, teamInSeason.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(teamInSeason.getId().intValue()));
    }

    @Test
    void getNonExistingTeamInSeason() {
        // Get the teamInSeason
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTeamInSeason() throws Exception {
        // Initialize the database
        teamInSeasonRepository.save(teamInSeason).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamInSeasonSearchRepository.save(teamInSeason).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());

        // Update the teamInSeason
        TeamInSeason updatedTeamInSeason = teamInSeasonRepository.findById(teamInSeason.getId()).block();
        TeamInSeasonDTO teamInSeasonDTO = teamInSeasonMapper.toDto(updatedTeamInSeason);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, teamInSeasonDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamInSeasonDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TeamInSeason in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTeamInSeasonToMatchAllProperties(updatedTeamInSeason);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TeamInSeason> teamInSeasonSearchList = Streamable.of(
                    teamInSeasonSearchRepository.findAll().collectList().block()
                ).toList();
                TeamInSeason testTeamInSeasonSearch = teamInSeasonSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTeamInSeasonAllPropertiesEquals(testTeamInSeasonSearch, updatedTeamInSeason);
                assertTeamInSeasonUpdatableFieldsEquals(testTeamInSeasonSearch, updatedTeamInSeason);
            });
    }

    @Test
    void putNonExistingTeamInSeason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());
        teamInSeason.setId(longCount.incrementAndGet());

        // Create the TeamInSeason
        TeamInSeasonDTO teamInSeasonDTO = teamInSeasonMapper.toDto(teamInSeason);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, teamInSeasonDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamInSeasonDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TeamInSeason in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTeamInSeason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());
        teamInSeason.setId(longCount.incrementAndGet());

        // Create the TeamInSeason
        TeamInSeasonDTO teamInSeasonDTO = teamInSeasonMapper.toDto(teamInSeason);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamInSeasonDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TeamInSeason in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTeamInSeason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());
        teamInSeason.setId(longCount.incrementAndGet());

        // Create the TeamInSeason
        TeamInSeasonDTO teamInSeasonDTO = teamInSeasonMapper.toDto(teamInSeason);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamInSeasonDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TeamInSeason in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTeamInSeasonWithPatch() throws Exception {
        // Initialize the database
        teamInSeasonRepository.save(teamInSeason).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teamInSeason using partial update
        TeamInSeason partialUpdatedTeamInSeason = new TeamInSeason();
        partialUpdatedTeamInSeason.setId(teamInSeason.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTeamInSeason.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTeamInSeason))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TeamInSeason in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeamInSeasonUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTeamInSeason, teamInSeason),
            getPersistedTeamInSeason(teamInSeason)
        );
    }

    @Test
    void fullUpdateTeamInSeasonWithPatch() throws Exception {
        // Initialize the database
        teamInSeasonRepository.save(teamInSeason).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teamInSeason using partial update
        TeamInSeason partialUpdatedTeamInSeason = new TeamInSeason();
        partialUpdatedTeamInSeason.setId(teamInSeason.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTeamInSeason.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTeamInSeason))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TeamInSeason in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeamInSeasonUpdatableFieldsEquals(partialUpdatedTeamInSeason, getPersistedTeamInSeason(partialUpdatedTeamInSeason));
    }

    @Test
    void patchNonExistingTeamInSeason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());
        teamInSeason.setId(longCount.incrementAndGet());

        // Create the TeamInSeason
        TeamInSeasonDTO teamInSeasonDTO = teamInSeasonMapper.toDto(teamInSeason);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, teamInSeasonDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(teamInSeasonDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TeamInSeason in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTeamInSeason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());
        teamInSeason.setId(longCount.incrementAndGet());

        // Create the TeamInSeason
        TeamInSeasonDTO teamInSeasonDTO = teamInSeasonMapper.toDto(teamInSeason);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(teamInSeasonDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TeamInSeason in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTeamInSeason() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());
        teamInSeason.setId(longCount.incrementAndGet());

        // Create the TeamInSeason
        TeamInSeasonDTO teamInSeasonDTO = teamInSeasonMapper.toDto(teamInSeason);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(teamInSeasonDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TeamInSeason in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTeamInSeason() {
        // Initialize the database
        teamInSeasonRepository.save(teamInSeason).block();
        teamInSeasonRepository.save(teamInSeason).block();
        teamInSeasonSearchRepository.save(teamInSeason).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the teamInSeason
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, teamInSeason.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamInSeasonSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTeamInSeason() {
        // Initialize the database
        teamInSeason = teamInSeasonRepository.save(teamInSeason).block();
        teamInSeasonSearchRepository.save(teamInSeason).block();

        // Search the teamInSeason
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + teamInSeason.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(teamInSeason.getId().intValue()));
    }

    protected long getRepositoryCount() {
        return teamInSeasonRepository.count().block();
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

    protected TeamInSeason getPersistedTeamInSeason(TeamInSeason teamInSeason) {
        return teamInSeasonRepository.findById(teamInSeason.getId()).block();
    }

    protected void assertPersistedTeamInSeasonToMatchAllProperties(TeamInSeason expectedTeamInSeason) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTeamInSeasonAllPropertiesEquals(expectedTeamInSeason, getPersistedTeamInSeason(expectedTeamInSeason));
        assertTeamInSeasonUpdatableFieldsEquals(expectedTeamInSeason, getPersistedTeamInSeason(expectedTeamInSeason));
    }

    protected void assertPersistedTeamInSeasonToMatchUpdatableProperties(TeamInSeason expectedTeamInSeason) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTeamInSeasonAllUpdatablePropertiesEquals(expectedTeamInSeason, getPersistedTeamInSeason(expectedTeamInSeason));
        assertTeamInSeasonUpdatableFieldsEquals(expectedTeamInSeason, getPersistedTeamInSeason(expectedTeamInSeason));
    }
}
