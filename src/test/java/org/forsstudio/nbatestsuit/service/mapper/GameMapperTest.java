package org.forsstudio.nbatestsuit.service.mapper;

import static org.forsstudio.nbatestsuit.domain.GameAsserts.*;
import static org.forsstudio.nbatestsuit.domain.GameTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameMapperTest {

    private GameMapper gameMapper;

    @BeforeEach
    void setUp() {
        gameMapper = new GameMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getGameSample1();
        var actual = gameMapper.toEntity(gameMapper.toDto(expected));
        assertGameAllPropertiesEquals(expected, actual);
    }
}
