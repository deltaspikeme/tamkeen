package com.tamkeen.backoffice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tamkeen.backoffice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserResponseDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserResponseDTO.class);
        UserResponseDTO userResponseDTO1 = new UserResponseDTO();
        userResponseDTO1.setId("id1");
        UserResponseDTO userResponseDTO2 = new UserResponseDTO();
        assertThat(userResponseDTO1).isNotEqualTo(userResponseDTO2);
        userResponseDTO2.setId(userResponseDTO1.getId());
        assertThat(userResponseDTO1).isEqualTo(userResponseDTO2);
        userResponseDTO2.setId("id2");
        assertThat(userResponseDTO1).isNotEqualTo(userResponseDTO2);
        userResponseDTO1.setId(null);
        assertThat(userResponseDTO1).isNotEqualTo(userResponseDTO2);
    }
}
