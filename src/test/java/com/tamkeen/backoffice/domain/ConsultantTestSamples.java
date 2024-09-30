package com.tamkeen.backoffice.domain;

import java.util.UUID;

public class ConsultantTestSamples {

    public static Consultant getConsultantSample1() {
        return new Consultant().id("id1").name("name1").expertise("expertise1").email("email1").phone("phone1");
    }

    public static Consultant getConsultantSample2() {
        return new Consultant().id("id2").name("name2").expertise("expertise2").email("email2").phone("phone2");
    }

    public static Consultant getConsultantRandomSampleGenerator() {
        return new Consultant()
            .id(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .expertise(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString());
    }
}
