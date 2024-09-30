package com.tamkeen.backoffice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tamkeen.backoffice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PersonalityTestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PersonalityTestDTO.class);
        PersonalityTestDTO personalityTestDTO1 = new PersonalityTestDTO();
        personalityTestDTO1.setId("id1");
        PersonalityTestDTO personalityTestDTO2 = new PersonalityTestDTO();
        assertThat(personalityTestDTO1).isNotEqualTo(personalityTestDTO2);
        personalityTestDTO2.setId(personalityTestDTO1.getId());
        assertThat(personalityTestDTO1).isEqualTo(personalityTestDTO2);
        personalityTestDTO2.setId("id2");
        assertThat(personalityTestDTO1).isNotEqualTo(personalityTestDTO2);
        personalityTestDTO1.setId(null);
        assertThat(personalityTestDTO1).isNotEqualTo(personalityTestDTO2);
    }
}
