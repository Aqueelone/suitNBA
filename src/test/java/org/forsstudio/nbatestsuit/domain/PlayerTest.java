package org.forsstudio.nbatestsuit.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forsstudio.nbatestsuit.domain.PlayerInGameTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.PlayerTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.forsstudio.nbatestsuit.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlayerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Player.class);
        Player player1 = getPlayerSample1();
        Player player2 = new Player();
        assertThat(player1).isNotEqualTo(player2);

        player2.setId(player1.getId());
        assertThat(player1).isEqualTo(player2);

        player2 = getPlayerSample2();
        assertThat(player1).isNotEqualTo(player2);
    }

    @Test
    void playerInGameTest() throws Exception {
        Player player = getPlayerRandomSampleGenerator();
        PlayerInGame playerInGameBack = getPlayerInGameRandomSampleGenerator();

        player.addPlayerInGame(playerInGameBack);
        assertThat(player.getPlayerInGames()).containsOnly(playerInGameBack);
        assertThat(playerInGameBack.getPlayer()).isEqualTo(player);

        player.removePlayerInGame(playerInGameBack);
        assertThat(player.getPlayerInGames()).doesNotContain(playerInGameBack);
        assertThat(playerInGameBack.getPlayer()).isNull();

        player.playerInGames(new HashSet<>(Set.of(playerInGameBack)));
        assertThat(player.getPlayerInGames()).containsOnly(playerInGameBack);
        assertThat(playerInGameBack.getPlayer()).isEqualTo(player);

        player.setPlayerInGames(new HashSet<>());
        assertThat(player.getPlayerInGames()).doesNotContain(playerInGameBack);
        assertThat(playerInGameBack.getPlayer()).isNull();
    }
}
