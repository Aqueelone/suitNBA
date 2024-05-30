package org.forsstudio.nbatestsuit.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forsstudio.nbatestsuit.domain.TeamInSeasonTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.TeamTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.forsstudio.nbatestsuit.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TeamTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Team.class);
        Team team1 = getTeamSample1();
        Team team2 = new Team();
        assertThat(team1).isNotEqualTo(team2);

        team2.setId(team1.getId());
        assertThat(team1).isEqualTo(team2);

        team2 = getTeamSample2();
        assertThat(team1).isNotEqualTo(team2);
    }

    @Test
    void teamInSeasonTest() throws Exception {
        Team team = getTeamRandomSampleGenerator();
        TeamInSeason teamInSeasonBack = getTeamInSeasonRandomSampleGenerator();

        team.addTeamInSeason(teamInSeasonBack);
        assertThat(team.getTeamInSeasons()).containsOnly(teamInSeasonBack);
        assertThat(teamInSeasonBack.getTeam()).isEqualTo(team);

        team.removeTeamInSeason(teamInSeasonBack);
        assertThat(team.getTeamInSeasons()).doesNotContain(teamInSeasonBack);
        assertThat(teamInSeasonBack.getTeam()).isNull();

        team.teamInSeasons(new HashSet<>(Set.of(teamInSeasonBack)));
        assertThat(team.getTeamInSeasons()).containsOnly(teamInSeasonBack);
        assertThat(teamInSeasonBack.getTeam()).isEqualTo(team);

        team.setTeamInSeasons(new HashSet<>());
        assertThat(team.getTeamInSeasons()).doesNotContain(teamInSeasonBack);
        assertThat(teamInSeasonBack.getTeam()).isNull();
    }
}
