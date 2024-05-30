package org.forsstudio.nbatestsuit.service.mapper;

import static org.forsstudio.nbatestsuit.domain.PlayerAsserts.*;
import static org.forsstudio.nbatestsuit.domain.PlayerTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerMapperTest {

    private PlayerMapper playerMapper;

    @BeforeEach
    void setUp() {
        playerMapper = new PlayerMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPlayerSample1();
        var actual = playerMapper.toEntity(playerMapper.toDto(expected));
        assertPlayerAllPropertiesEquals(expected, actual);
    }
}
