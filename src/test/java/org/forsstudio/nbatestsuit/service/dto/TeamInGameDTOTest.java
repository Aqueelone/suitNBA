package org.forsstudio.nbatestsuit.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.forsstudio.nbatestsuit.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TeamInGameDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeamInGameDTO.class);
        TeamInGameDTO teamInGameDTO1 = new TeamInGameDTO();
        teamInGameDTO1.setId(1L);
        TeamInGameDTO teamInGameDTO2 = new TeamInGameDTO();
        assertThat(teamInGameDTO1).isNotEqualTo(teamInGameDTO2);
        teamInGameDTO2.setId(teamInGameDTO1.getId());
        assertThat(teamInGameDTO1).isEqualTo(teamInGameDTO2);
        teamInGameDTO2.setId(2L);
        assertThat(teamInGameDTO1).isNotEqualTo(teamInGameDTO2);
        teamInGameDTO1.setId(null);
        assertThat(teamInGameDTO1).isNotEqualTo(teamInGameDTO2);
    }
}
