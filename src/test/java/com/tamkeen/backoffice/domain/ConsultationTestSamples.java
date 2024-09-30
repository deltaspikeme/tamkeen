package com.tamkeen.backoffice.domain;

import java.util.UUID;

public class ConsultationTestSamples {

    public static Consultation getConsultationSample1() {
        return new Consultation().id("id1").consultantName("consultantName1");
    }

    public static Consultation getConsultationSample2() {
        return new Consultation().id("id2").consultantName("consultantName2");
    }

    public static Consultation getConsultationRandomSampleGenerator() {
        return new Consultation().id(UUID.randomUUID().toString()).consultantName(UUID.randomUUID().toString());
    }
}
