package org.forsstudio.nbatestsuit.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.forsstudio.nbatestsuit.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlayerInGameDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlayerInGameDTO.class);
        PlayerInGameDTO playerInGameDTO1 = new PlayerInGameDTO();
        playerInGameDTO1.setId(1L);
        PlayerInGameDTO playerInGameDTO2 = new PlayerInGameDTO();
        assertThat(playerInGameDTO1).isNotEqualTo(playerInGameDTO2);
        playerInGameDTO2.setId(playerInGameDTO1.getId());
        assertThat(playerInGameDTO1).isEqualTo(playerInGameDTO2);
        playerInGameDTO2.setId(2L);
        assertThat(playerInGameDTO1).isNotEqualTo(playerInGameDTO2);
        playerInGameDTO1.setId(null);
        assertThat(playerInGameDTO1).isNotEqualTo(playerInGameDTO2);
    }
}
