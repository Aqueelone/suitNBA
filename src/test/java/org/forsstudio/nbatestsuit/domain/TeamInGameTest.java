package org.forsstudio.nbatestsuit.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forsstudio.nbatestsuit.domain.GameTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.PlayerInGameTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.TeamInGameTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.TeamInSeasonTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.forsstudio.nbatestsuit.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TeamInGameTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeamInGame.class);
        TeamInGame teamInGame1 = getTeamInGameSample1();
        TeamInGame teamInGame2 = new TeamInGame();
        assertThat(teamInGame1).isNotEqualTo(teamInGame2);

        teamInGame2.setId(teamInGame1.getId());
        assertThat(teamInGame1).isEqualTo(teamInGame2);

        teamInGame2 = getTeamInGameSample2();
        assertThat(teamInGame1).isNotEqualTo(teamInGame2);
    }

    @Test
    void playerInGameTest() throws Exception {
        TeamInGame teamInGame = getTeamInGameRandomSampleGenerator();
        PlayerInGame playerInGameBack = getPlayerInGameRandomSampleGenerator();

        teamInGame.addPlayerInGame(playerInGameBack);
        assertThat(teamInGame.getPlayerInGames()).containsOnly(playerInGameBack);
        assertThat(playerInGameBack.getTeam()).isEqualTo(teamInGame);

        teamInGame.removePlayerInGame(playerInGameBack);
        assertThat(teamInGame.getPlayerInGames()).doesNotContain(playerInGameBack);
        assertThat(playerInGameBack.getTeam()).isNull();

        teamInGame.playerInGames(new HashSet<>(Set.of(playerInGameBack)));
        assertThat(teamInGame.getPlayerInGames()).containsOnly(playerInGameBack);
        assertThat(playerInGameBack.getTeam()).isEqualTo(teamInGame);

        teamInGame.setPlayerInGames(new HashSet<>());
        assertThat(teamInGame.getPlayerInGames()).doesNotContain(playerInGameBack);
        assertThat(playerInGameBack.getTeam()).isNull();
    }

    @Test
    void teamTest() throws Exception {
        TeamInGame teamInGame = getTeamInGameRandomSampleGenerator();
        TeamInSeason teamInSeasonBack = getTeamInSeasonRandomSampleGenerator();

        teamInGame.setTeam(teamInSeasonBack);
        assertThat(teamInGame.getTeam()).isEqualTo(teamInSeasonBack);

        teamInGame.team(null);
        assertThat(teamInGame.getTeam()).isNull();
    }

    @Test
    void gameTest() throws Exception {
        TeamInGame teamInGame = getTeamInGameRandomSampleGenerator();
        Game gameBack = getGameRandomSampleGenerator();

        teamInGame.setGame(gameBack);
        assertThat(teamInGame.getGame()).isEqualTo(gameBack);

        teamInGame.game(null);
        assertThat(teamInGame.getGame()).isNull();
    }
}
