package org.forsstudio.nbatestsuit.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forsstudio.nbatestsuit.domain.GameTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.PlayerInGameTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.PlayerTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.TeamInGameTestSamples.*;

import org.forsstudio.nbatestsuit.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlayerInGameTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlayerInGame.class);
        PlayerInGame playerInGame1 = getPlayerInGameSample1();
        PlayerInGame playerInGame2 = new PlayerInGame();
        assertThat(playerInGame1).isNotEqualTo(playerInGame2);

        playerInGame2.setId(playerInGame1.getId());
        assertThat(playerInGame1).isEqualTo(playerInGame2);

        playerInGame2 = getPlayerInGameSample2();
        assertThat(playerInGame1).isNotEqualTo(playerInGame2);
    }

    @Test
    void teamTest() throws Exception {
        PlayerInGame playerInGame = getPlayerInGameRandomSampleGenerator();
        TeamInGame teamInGameBack = getTeamInGameRandomSampleGenerator();

        playerInGame.setTeam(teamInGameBack);
        assertThat(playerInGame.getTeam()).isEqualTo(teamInGameBack);

        playerInGame.team(null);
        assertThat(playerInGame.getTeam()).isNull();
    }

    @Test
    void playerTest() throws Exception {
        PlayerInGame playerInGame = getPlayerInGameRandomSampleGenerator();
        Player playerBack = getPlayerRandomSampleGenerator();

        playerInGame.setPlayer(playerBack);
        assertThat(playerInGame.getPlayer()).isEqualTo(playerBack);

        playerInGame.player(null);
        assertThat(playerInGame.getPlayer()).isNull();
    }

    @Test
    void gameTest() throws Exception {
        PlayerInGame playerInGame = getPlayerInGameRandomSampleGenerator();
        Game gameBack = getGameRandomSampleGenerator();

        playerInGame.setGame(gameBack);
        assertThat(playerInGame.getGame()).isEqualTo(gameBack);

        playerInGame.game(null);
        assertThat(playerInGame.getGame()).isNull();
    }
}
