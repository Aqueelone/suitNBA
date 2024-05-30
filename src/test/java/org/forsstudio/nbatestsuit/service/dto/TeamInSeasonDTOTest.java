package org.forsstudio.nbatestsuit.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.forsstudio.nbatestsuit.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TeamInSeasonDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeamInSeasonDTO.class);
        TeamInSeasonDTO teamInSeasonDTO1 = new TeamInSeasonDTO();
        teamInSeasonDTO1.setId(1L);
        TeamInSeasonDTO teamInSeasonDTO2 = new TeamInSeasonDTO();
        assertThat(teamInSeasonDTO1).isNotEqualTo(teamInSeasonDTO2);
        teamInSeasonDTO2.setId(teamInSeasonDTO1.getId());
        assertThat(teamInSeasonDTO1).isEqualTo(teamInSeasonDTO2);
        teamInSeasonDTO2.setId(2L);
        assertThat(teamInSeasonDTO1).isNotEqualTo(teamInSeasonDTO2);
        teamInSeasonDTO1.setId(null);
        assertThat(teamInSeasonDTO1).isNotEqualTo(teamInSeasonDTO2);
    }
}
