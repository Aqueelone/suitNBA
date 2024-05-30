package org.forsstudio.nbatestsuit.service.mapper;

import static org.forsstudio.nbatestsuit.domain.TeamInGameAsserts.*;
import static org.forsstudio.nbatestsuit.domain.TeamInGameTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TeamInGameMapperTest {

    private TeamInGameMapper teamInGameMapper;

    @BeforeEach
    void setUp() {
        teamInGameMapper = new TeamInGameMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTeamInGameSample1();
        var actual = teamInGameMapper.toEntity(teamInGameMapper.toDto(expected));
        assertTeamInGameAllPropertiesEquals(expected, actual);
    }
}
