package com.tamkeen.backoffice.domain;

import java.util.UUID;

public class PersonalityTypeTestSamples {

    public static PersonalityType getPersonalityTypeSample1() {
        return new PersonalityType().id("id1").typeCode("typeCode1");
    }

    public static PersonalityType getPersonalityTypeSample2() {
        return new PersonalityType().id("id2").typeCode("typeCode2");
    }

    public static PersonalityType getPersonalityTypeRandomSampleGenerator() {
        return new PersonalityType().id(UUID.randomUUID().toString()).typeCode(UUID.randomUUID().toString());
    }
}
