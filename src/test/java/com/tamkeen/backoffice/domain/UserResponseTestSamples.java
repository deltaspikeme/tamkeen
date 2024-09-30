package com.tamkeen.backoffice.domain;

import java.util.UUID;

public class UserResponseTestSamples {

    public static UserResponse getUserResponseSample1() {
        return new UserResponse().id("id1");
    }

    public static UserResponse getUserResponseSample2() {
        return new UserResponse().id("id2");
    }

    public static UserResponse getUserResponseRandomSampleGenerator() {
        return new UserResponse().id(UUID.randomUUID().toString());
    }
}
