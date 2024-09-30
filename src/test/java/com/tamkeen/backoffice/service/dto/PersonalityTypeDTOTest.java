package com.tamkeen.backoffice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tamkeen.backoffice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PersonalityTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PersonalityTypeDTO.class);
        PersonalityTypeDTO personalityTypeDTO1 = new PersonalityTypeDTO();
        personalityTypeDTO1.setId("id1");
        PersonalityTypeDTO personalityTypeDTO2 = new PersonalityTypeDTO();
        assertThat(personalityTypeDTO1).isNotEqualTo(personalityTypeDTO2);
        personalityTypeDTO2.setId(personalityTypeDTO1.getId());
        assertThat(personalityTypeDTO1).isEqualTo(personalityTypeDTO2);
        personalityTypeDTO2.setId("id2");
        assertThat(personalityTypeDTO1).isNotEqualTo(personalityTypeDTO2);
        personalityTypeDTO1.setId(null);
        assertThat(personalityTypeDTO1).isNotEqualTo(personalityTypeDTO2);
    }
}
