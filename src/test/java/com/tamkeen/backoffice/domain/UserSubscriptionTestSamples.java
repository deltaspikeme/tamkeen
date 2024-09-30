package com.tamkeen.backoffice.domain;

import java.util.UUID;

public class UserSubscriptionTestSamples {

    public static UserSubscription getUserSubscriptionSample1() {
        return new UserSubscription().id("id1");
    }

    public static UserSubscription getUserSubscriptionSample2() {
        return new UserSubscription().id("id2");
    }

    public static UserSubscription getUserSubscriptionRandomSampleGenerator() {
        return new UserSubscription().id(UUID.randomUUID().toString());
    }
}
