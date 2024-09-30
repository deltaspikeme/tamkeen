package com.tamkeen.backoffice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tamkeen.backoffice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ConsultantDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ConsultantDTO.class);
        ConsultantDTO consultantDTO1 = new ConsultantDTO();
        consultantDTO1.setId("id1");
        ConsultantDTO consultantDTO2 = new ConsultantDTO();
        assertThat(consultantDTO1).isNotEqualTo(consultantDTO2);
        consultantDTO2.setId(consultantDTO1.getId());
        assertThat(consultantDTO1).isEqualTo(consultantDTO2);
        consultantDTO2.setId("id2");
        assertThat(consultantDTO1).isNotEqualTo(consultantDTO2);
        consultantDTO1.setId(null);
        assertThat(consultantDTO1).isNotEqualTo(consultantDTO2);
    }
}
