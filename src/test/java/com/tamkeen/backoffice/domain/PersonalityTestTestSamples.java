package com.tamkeen.backoffice.domain;

import java.util.UUID;

public class PersonalityTestTestSamples {

    public static PersonalityTest getPersonalityTestSample1() {
        return new PersonalityTest().id("id1").testName("testName1");
    }

    public static PersonalityTest getPersonalityTestSample2() {
        return new PersonalityTest().id("id2").testName("testName2");
    }

    public static PersonalityTest getPersonalityTestRandomSampleGenerator() {
        return new PersonalityTest().id(UUID.randomUUID().toString()).testName(UUID.randomUUID().toString());
    }
}
