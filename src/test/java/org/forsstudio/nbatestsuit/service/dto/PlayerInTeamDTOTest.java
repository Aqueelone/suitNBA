package org.forsstudio.nbatestsuit.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.forsstudio.nbatestsuit.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlayerInTeamDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlayerInTeamDTO.class);
        PlayerInTeamDTO playerInTeamDTO1 = new PlayerInTeamDTO();
        playerInTeamDTO1.setId(1L);
        PlayerInTeamDTO playerInTeamDTO2 = new PlayerInTeamDTO();
        assertThat(playerInTeamDTO1).isNotEqualTo(playerInTeamDTO2);
        playerInTeamDTO2.setId(playerInTeamDTO1.getId());
        assertThat(playerInTeamDTO1).isEqualTo(playerInTeamDTO2);
        playerInTeamDTO2.setId(2L);
        assertThat(playerInTeamDTO1).isNotEqualTo(playerInTeamDTO2);
        playerInTeamDTO1.setId(null);
        assertThat(playerInTeamDTO1).isNotEqualTo(playerInTeamDTO2);
    }
}
