package org.forsstudio.nbatestsuit.service.mapper;

import static org.forsstudio.nbatestsuit.domain.PlayerInTeamAsserts.*;
import static org.forsstudio.nbatestsuit.domain.PlayerInTeamTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerInTeamMapperTest {

    private PlayerInTeamMapper playerInTeamMapper;

    @BeforeEach
    void setUp() {
        playerInTeamMapper = new PlayerInTeamMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPlayerInTeamSample1();
        var actual = playerInTeamMapper.toEntity(playerInTeamMapper.toDto(expected));
        assertPlayerInTeamAllPropertiesEquals(expected, actual);
    }
}
