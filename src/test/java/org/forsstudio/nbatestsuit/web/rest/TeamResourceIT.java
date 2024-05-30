package org.forsstudio.nbatestsuit.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.forsstudio.nbatestsuit.domain.TeamAsserts.*;
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
import org.forsstudio.nbatestsuit.domain.Team;
import org.forsstudio.nbatestsuit.repository.EntityManager;
import org.forsstudio.nbatestsuit.repository.TeamRepository;
import org.forsstudio.nbatestsuit.repository.search.TeamSearchRepository;
import org.forsstudio.nbatestsuit.service.dto.TeamDTO;
import org.forsstudio.nbatestsuit.service.mapper.TeamMapper;
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
 * Integration tests for the {@link TeamResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TeamResourceIT {

    private static final String DEFAULT_TEAM_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TEAM_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/teams";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/teams/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamSearchRepository teamSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Team team;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Team createEntity(EntityManager em) {
        Team team = new Team().teamName(DEFAULT_TEAM_NAME);
        return team;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Team createUpdatedEntity(EntityManager em) {
        Team team = new Team().teamName(UPDATED_TEAM_NAME);
        return team;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Team.class).block();
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
        teamSearchRepository.deleteAll().block();
        assertThat(teamSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        team = createEntity(em);
    }

    @Test
    void createTeam() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());
        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);
        var returnedTeamDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TeamDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Team in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTeam = teamMapper.toEntity(returnedTeamDTO);
        assertTeamUpdatableFieldsEquals(returnedTeam, getPersistedTeam(returnedTeam));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
    }

    @Test
    void createTeamWithExistingId() throws Exception {
        // Create the Team with an existing ID
        team.setId(1L);
        TeamDTO teamDTO = teamMapper.toDto(team);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTeams() {
        // Initialize the database
        teamRepository.save(team).block();

        // Get all the teamList
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
            .value(hasItem(team.getId().intValue()))
            .jsonPath("$.[*].teamName")
            .value(hasItem(DEFAULT_TEAM_NAME));
    }

    @Test
    void getTeam() {
        // Initialize the database
        teamRepository.save(team).block();

        // Get the team
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, team.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(team.getId().intValue()))
            .jsonPath("$.teamName")
            .value(is(DEFAULT_TEAM_NAME));
    }

    @Test
    void getNonExistingTeam() {
        // Get the team
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTeam() throws Exception {
        // Initialize the database
        teamRepository.save(team).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamSearchRepository.save(team).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());

        // Update the team
        Team updatedTeam = teamRepository.findById(team.getId()).block();
        updatedTeam.teamName(UPDATED_TEAM_NAME);
        TeamDTO teamDTO = teamMapper.toDto(updatedTeam);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, teamDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTeamToMatchAllProperties(updatedTeam);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Team> teamSearchList = Streamable.of(teamSearchRepository.findAll().collectList().block()).toList();
                Team testTeamSearch = teamSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertTeamAllPropertiesEquals(testTeamSearch, updatedTeam);
                assertTeamUpdatableFieldsEquals(testTeamSearch, updatedTeam);
            });
    }

    @Test
    void putNonExistingTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, teamDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTeamWithPatch() throws Exception {
        // Initialize the database
        teamRepository.save(team).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the team using partial update
        Team partialUpdatedTeam = new Team();
        partialUpdatedTeam.setId(team.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTeam.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTeam))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Team in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeamUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTeam, team), getPersistedTeam(team));
    }

    @Test
    void fullUpdateTeamWithPatch() throws Exception {
        // Initialize the database
        teamRepository.save(team).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the team using partial update
        Team partialUpdatedTeam = new Team();
        partialUpdatedTeam.setId(team.getId());

        partialUpdatedTeam.teamName(UPDATED_TEAM_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTeam.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTeam))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Team in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeamUpdatableFieldsEquals(partialUpdatedTeam, getPersistedTeam(partialUpdatedTeam));
    }

    @Test
    void patchNonExistingTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, teamDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTeam() {
        // Initialize the database
        teamRepository.save(team).block();
        teamRepository.save(team).block();
        teamSearchRepository.save(team).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the team
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, team.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(teamSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTeam() {
        // Initialize the database
        team = teamRepository.save(team).block();
        teamSearchRepository.save(team).block();

        // Search the team
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + team.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(team.getId().intValue()))
            .jsonPath("$.[*].teamName")
            .value(hasItem(DEFAULT_TEAM_NAME));
    }

    protected long getRepositoryCount() {
        return teamRepository.count().block();
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

    protected Team getPersistedTeam(Team team) {
        return teamRepository.findById(team.getId()).block();
    }

    protected void assertPersistedTeamToMatchAllProperties(Team expectedTeam) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTeamAllPropertiesEquals(expectedTeam, getPersistedTeam(expectedTeam));
        assertTeamUpdatableFieldsEquals(expectedTeam, getPersistedTeam(expectedTeam));
    }

    protected void assertPersistedTeamToMatchUpdatableProperties(Team expectedTeam) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTeamAllUpdatablePropertiesEquals(expectedTeam, getPersistedTeam(expectedTeam));
        assertTeamUpdatableFieldsEquals(expectedTeam, getPersistedTeam(expectedTeam));
    }
}
