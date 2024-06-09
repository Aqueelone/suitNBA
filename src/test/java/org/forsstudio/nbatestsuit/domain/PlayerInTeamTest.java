package org.forsstudio.nbatestsuit.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forsstudio.nbatestsuit.domain.PlayerInTeamTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.PlayerTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.TeamInSeasonTestSamples.*;

import org.forsstudio.nbatestsuit.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlayerInTeamTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlayerInTeam.class);
        PlayerInTeam playerInTeam1 = getPlayerInTeamSample1();
        PlayerInTeam playerInTeam2 = new PlayerInTeam();
        assertThat(playerInTeam1).isNotEqualTo(playerInTeam2);

        playerInTeam2.setId(playerInTeam1.getId());
        assertThat(playerInTeam1).isEqualTo(playerInTeam2);

        playerInTeam2 = getPlayerInTeamSample2();
        assertThat(playerInTeam1).isNotEqualTo(playerInTeam2);
    }

    @Test
    void playerTest() throws Exception {
        PlayerInTeam playerInTeam = getPlayerInTeamRandomSampleGenerator();
        Player playerBack = getPlayerRandomSampleGenerator();

        playerInTeam.setPlayer(playerBack);
        assertThat(playerInTeam.getPlayer()).isEqualTo(playerBack);

        playerInTeam.player(null);
        assertThat(playerInTeam.getPlayer()).isNull();
    }

    @Test
    void teamInSeasonTest() throws Exception {
        PlayerInTeam playerInTeam = getPlayerInTeamRandomSampleGenerator();
        TeamInSeason teamInSeasonBack = getTeamInSeasonRandomSampleGenerator();

        playerInTeam.setTeamInSeason(teamInSeasonBack);
        assertThat(playerInTeam.getTeamInSeason()).isEqualTo(teamInSeasonBack);

        playerInTeam.teamInSeason(null);
        assertThat(playerInTeam.getTeamInSeason()).isNull();
    }
}
