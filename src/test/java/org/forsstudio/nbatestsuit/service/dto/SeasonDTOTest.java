package org.forsstudio.nbatestsuit.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.forsstudio.nbatestsuit.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SeasonDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SeasonDTO.class);
        SeasonDTO seasonDTO1 = new SeasonDTO();
        seasonDTO1.setId(1L);
        SeasonDTO seasonDTO2 = new SeasonDTO();
        assertThat(seasonDTO1).isNotEqualTo(seasonDTO2);
        seasonDTO2.setId(seasonDTO1.getId());
        assertThat(seasonDTO1).isEqualTo(seasonDTO2);
        seasonDTO2.setId(2L);
        assertThat(seasonDTO1).isNotEqualTo(seasonDTO2);
        seasonDTO1.setId(null);
        assertThat(seasonDTO1).isNotEqualTo(seasonDTO2);
    }
}
