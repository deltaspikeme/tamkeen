package com.tamkeen.backoffice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tamkeen.backoffice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserSubscriptionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserSubscriptionDTO.class);
        UserSubscriptionDTO userSubscriptionDTO1 = new UserSubscriptionDTO();
        userSubscriptionDTO1.setId("id1");
        UserSubscriptionDTO userSubscriptionDTO2 = new UserSubscriptionDTO();
        assertThat(userSubscriptionDTO1).isNotEqualTo(userSubscriptionDTO2);
        userSubscriptionDTO2.setId(userSubscriptionDTO1.getId());
        assertThat(userSubscriptionDTO1).isEqualTo(userSubscriptionDTO2);
        userSubscriptionDTO2.setId("id2");
        assertThat(userSubscriptionDTO1).isNotEqualTo(userSubscriptionDTO2);
        userSubscriptionDTO1.setId(null);
        assertThat(userSubscriptionDTO1).isNotEqualTo(userSubscriptionDTO2);
    }
}
