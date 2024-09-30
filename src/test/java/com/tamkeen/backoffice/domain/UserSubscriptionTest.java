package com.tamkeen.backoffice.domain;

import static com.tamkeen.backoffice.domain.UserSubscriptionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.tamkeen.backoffice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserSubscriptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserSubscription.class);
        UserSubscription userSubscription1 = getUserSubscriptionSample1();
        UserSubscription userSubscription2 = new UserSubscription();
        assertThat(userSubscription1).isNotEqualTo(userSubscription2);

        userSubscription2.setId(userSubscription1.getId());
        assertThat(userSubscription1).isEqualTo(userSubscription2);

        userSubscription2 = getUserSubscriptionSample2();
        assertThat(userSubscription1).isNotEqualTo(userSubscription2);
    }
}
