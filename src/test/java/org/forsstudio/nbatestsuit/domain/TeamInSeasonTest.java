package org.forsstudio.nbatestsuit.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forsstudio.nbatestsuit.domain.PlayerInTeamTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.SeasonTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.TeamInGameTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.TeamInSeasonTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.TeamTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.forsstudio.nbatestsuit.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TeamInSeasonTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeamInSeason.class);
        TeamInSeason teamInSeason1 = getTeamInSeasonSample1();
        TeamInSeason teamInSeason2 = new TeamInSeason();
        assertThat(teamInSeason1).isNotEqualTo(teamInSeason2);

        teamInSeason2.setId(teamInSeason1.getId());
        assertThat(teamInSeason1).isEqualTo(teamInSeason2);

        teamInSeason2 = getTeamInSeasonSample2();
        assertThat(teamInSeason1).isNotEqualTo(teamInSeason2);
    }

    @Test
    void teamInGameTest() throws Exception {
        TeamInSeason teamInSeason = getTeamInSeasonRandomSampleGenerator();
        TeamInGame teamInGameBack = getTeamInGameRandomSampleGenerator();

        teamInSeason.addTeamInGame(teamInGameBack);
        assertThat(teamInSeason.getTeamInGames()).containsOnly(teamInGameBack);
        assertThat(teamInGameBack.getTeam()).isEqualTo(teamInSeason);

        teamInSeason.removeTeamInGame(teamInGameBack);
        assertThat(teamInSeason.getTeamInGames()).doesNotContain(teamInGameBack);
        assertThat(teamInGameBack.getTeam()).isNull();

        teamInSeason.teamInGames(new HashSet<>(Set.of(teamInGameBack)));
        assertThat(teamInSeason.getTeamInGames()).containsOnly(teamInGameBack);
        assertThat(teamInGameBack.getTeam()).isEqualTo(teamInSeason);

        teamInSeason.setTeamInGames(new HashSet<>());
        assertThat(teamInSeason.getTeamInGames()).doesNotContain(teamInGameBack);
        assertThat(teamInGameBack.getTeam()).isNull();
    }

    @Test
    void teamTest() throws Exception {
        TeamInSeason teamInSeason = getTeamInSeasonRandomSampleGenerator();
        Team teamBack = getTeamRandomSampleGenerator();

        teamInSeason.setTeam(teamBack);
        assertThat(teamInSeason.getTeam()).isEqualTo(teamBack);

        teamInSeason.team(null);
        assertThat(teamInSeason.getTeam()).isNull();
    }

    @Test
    void seasonTest() throws Exception {
        TeamInSeason teamInSeason = getTeamInSeasonRandomSampleGenerator();
        Season seasonBack = getSeasonRandomSampleGenerator();

        teamInSeason.setSeason(seasonBack);
        assertThat(teamInSeason.getSeason()).isEqualTo(seasonBack);

        teamInSeason.season(null);
        assertThat(teamInSeason.getSeason()).isNull();
    }

    @Test
    void playerInTeamTest() throws Exception {
        TeamInSeason teamInSeason = getTeamInSeasonRandomSampleGenerator();
        PlayerInTeam playerInTeamBack = getPlayerInTeamRandomSampleGenerator();

        teamInSeason.addPlayerInTeam(playerInTeamBack);
        assertThat(teamInSeason.getPlayerInTeams()).containsOnly(playerInTeamBack);
        assertThat(playerInTeamBack.getTeamInSeason()).isEqualTo(teamInSeason);

        teamInSeason.removePlayerInTeam(playerInTeamBack);
        assertThat(teamInSeason.getPlayerInTeams()).doesNotContain(playerInTeamBack);
        assertThat(playerInTeamBack.getTeamInSeason()).isNull();

        teamInSeason.playerInTeams(new HashSet<>(Set.of(playerInTeamBack)));
        assertThat(teamInSeason.getPlayerInTeams()).containsOnly(playerInTeamBack);
        assertThat(playerInTeamBack.getTeamInSeason()).isEqualTo(teamInSeason);

        teamInSeason.setPlayerInTeams(new HashSet<>());
        assertThat(teamInSeason.getPlayerInTeams()).doesNotContain(playerInTeamBack);
        assertThat(playerInTeamBack.getTeamInSeason()).isNull();
    }
}
