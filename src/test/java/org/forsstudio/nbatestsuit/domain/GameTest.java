package org.forsstudio.nbatestsuit.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forsstudio.nbatestsuit.domain.GameTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.PlayerInGameTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.SeasonTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.TeamInGameTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.forsstudio.nbatestsuit.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GameTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Game.class);
        Game game1 = getGameSample1();
        Game game2 = new Game();
        assertThat(game1).isNotEqualTo(game2);

        game2.setId(game1.getId());
        assertThat(game1).isEqualTo(game2);

        game2 = getGameSample2();
        assertThat(game1).isNotEqualTo(game2);
    }

    @Test
    void playerInGameTest() throws Exception {
        Game game = getGameRandomSampleGenerator();
        PlayerInGame playerInGameBack = getPlayerInGameRandomSampleGenerator();

        game.addPlayerInGame(playerInGameBack);
        assertThat(game.getPlayerInGames()).containsOnly(playerInGameBack);
        assertThat(playerInGameBack.getGame()).isEqualTo(game);

        game.removePlayerInGame(playerInGameBack);
        assertThat(game.getPlayerInGames()).doesNotContain(playerInGameBack);
        assertThat(playerInGameBack.getGame()).isNull();

        game.playerInGames(new HashSet<>(Set.of(playerInGameBack)));
        assertThat(game.getPlayerInGames()).containsOnly(playerInGameBack);
        assertThat(playerInGameBack.getGame()).isEqualTo(game);

        game.setPlayerInGames(new HashSet<>());
        assertThat(game.getPlayerInGames()).doesNotContain(playerInGameBack);
        assertThat(playerInGameBack.getGame()).isNull();
    }

    @Test
    void teamInGameTest() throws Exception {
        Game game = getGameRandomSampleGenerator();
        TeamInGame teamInGameBack = getTeamInGameRandomSampleGenerator();

        game.addTeamInGame(teamInGameBack);
        assertThat(game.getTeamInGames()).containsOnly(teamInGameBack);
        assertThat(teamInGameBack.getGame()).isEqualTo(game);

        game.removeTeamInGame(teamInGameBack);
        assertThat(game.getTeamInGames()).doesNotContain(teamInGameBack);
        assertThat(teamInGameBack.getGame()).isNull();

        game.teamInGames(new HashSet<>(Set.of(teamInGameBack)));
        assertThat(game.getTeamInGames()).containsOnly(teamInGameBack);
        assertThat(teamInGameBack.getGame()).isEqualTo(game);

        game.setTeamInGames(new HashSet<>());
        assertThat(game.getTeamInGames()).doesNotContain(teamInGameBack);
        assertThat(teamInGameBack.getGame()).isNull();
    }

    @Test
    void seasonTest() throws Exception {
        Game game = getGameRandomSampleGenerator();
        Season seasonBack = getSeasonRandomSampleGenerator();

        game.setSeason(seasonBack);
        assertThat(game.getSeason()).isEqualTo(seasonBack);

        game.season(null);
        assertThat(game.getSeason()).isNull();
    }
}
