package org.forsstudio.nbatestsuit.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forsstudio.nbatestsuit.domain.GameTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.SeasonTestSamples.*;
import static org.forsstudio.nbatestsuit.domain.TeamInSeasonTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.forsstudio.nbatestsuit.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SeasonTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Season.class);
        Season season1 = getSeasonSample1();
        Season season2 = new Season();
        assertThat(season1).isNotEqualTo(season2);

        season2.setId(season1.getId());
        assertThat(season1).isEqualTo(season2);

        season2 = getSeasonSample2();
        assertThat(season1).isNotEqualTo(season2);
    }

    @Test
    void teamInSeasonTest() throws Exception {
        Season season = getSeasonRandomSampleGenerator();
        TeamInSeason teamInSeasonBack = getTeamInSeasonRandomSampleGenerator();

        season.addTeamInSeason(teamInSeasonBack);
        assertThat(season.getTeamInSeasons()).containsOnly(teamInSeasonBack);
        assertThat(teamInSeasonBack.getSeason()).isEqualTo(season);

        season.removeTeamInSeason(teamInSeasonBack);
        assertThat(season.getTeamInSeasons()).doesNotContain(teamInSeasonBack);
        assertThat(teamInSeasonBack.getSeason()).isNull();

        season.teamInSeasons(new HashSet<>(Set.of(teamInSeasonBack)));
        assertThat(season.getTeamInSeasons()).containsOnly(teamInSeasonBack);
        assertThat(teamInSeasonBack.getSeason()).isEqualTo(season);

        season.setTeamInSeasons(new HashSet<>());
        assertThat(season.getTeamInSeasons()).doesNotContain(teamInSeasonBack);
        assertThat(teamInSeasonBack.getSeason()).isNull();
    }

    @Test
    void gameTest() throws Exception {
        Season season = getSeasonRandomSampleGenerator();
        Game gameBack = getGameRandomSampleGenerator();

        season.addGame(gameBack);
        assertThat(season.getGames()).containsOnly(gameBack);
        assertThat(gameBack.getSeason()).isEqualTo(season);

        season.removeGame(gameBack);
        assertThat(season.getGames()).doesNotContain(gameBack);
        assertThat(gameBack.getSeason()).isNull();

        season.games(new HashSet<>(Set.of(gameBack)));
        assertThat(season.getGames()).containsOnly(gameBack);
        assertThat(gameBack.getSeason()).isEqualTo(season);

        season.setGames(new HashSet<>());
        assertThat(season.getGames()).doesNotContain(gameBack);
        assertThat(gameBack.getSeason()).isNull();
    }
}
