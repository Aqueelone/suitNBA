package org.forsstudio.nbatestsuit.service.mapper;

import static org.forsstudio.nbatestsuit.domain.PlayerInGameAsserts.*;
import static org.forsstudio.nbatestsuit.domain.PlayerInGameTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerInGameMapperTest {

    private PlayerInGameMapper playerInGameMapper;

    @BeforeEach
    void setUp() {
        playerInGameMapper = new PlayerInGameMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPlayerInGameSample1();
        var actual = playerInGameMapper.toEntity(playerInGameMapper.toDto(expected));
        assertPlayerInGameAllPropertiesEquals(expected, actual);
    }
}
