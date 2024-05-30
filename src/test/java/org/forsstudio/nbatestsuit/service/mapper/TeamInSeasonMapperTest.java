package org.forsstudio.nbatestsuit.service.mapper;

import static org.forsstudio.nbatestsuit.domain.TeamInSeasonAsserts.*;
import static org.forsstudio.nbatestsuit.domain.TeamInSeasonTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TeamInSeasonMapperTest {

    private TeamInSeasonMapper teamInSeasonMapper;

    @BeforeEach
    void setUp() {
        teamInSeasonMapper = new TeamInSeasonMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTeamInSeasonSample1();
        var actual = teamInSeasonMapper.toEntity(teamInSeasonMapper.toDto(expected));
        assertTeamInSeasonAllPropertiesEquals(expected, actual);
    }
}
