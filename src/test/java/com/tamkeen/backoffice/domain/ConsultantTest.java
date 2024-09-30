package com.tamkeen.backoffice.domain;

import static com.tamkeen.backoffice.domain.ConsultantTestSamples.*;
import static com.tamkeen.backoffice.domain.ConsultationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.tamkeen.backoffice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ConsultantTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Consultant.class);
        Consultant consultant1 = getConsultantSample1();
        Consultant consultant2 = new Consultant();
        assertThat(consultant1).isNotEqualTo(consultant2);

        consultant2.setId(consultant1.getId());
        assertThat(consultant1).isEqualTo(consultant2);

        consultant2 = getConsultantSample2();
        assertThat(consultant1).isNotEqualTo(consultant2);
    }

    @Test
    void consultationTest() {
        Consultant consultant = getConsultantRandomSampleGenerator();
        Consultation consultationBack = getConsultationRandomSampleGenerator();

        consultant.addConsultation(consultationBack);
        assertThat(consultant.getConsultations()).containsOnly(consultationBack);
        assertThat(consultationBack.getConsultant()).isEqualTo(consultant);

        consultant.removeConsultation(consultationBack);
        assertThat(consultant.getConsultations()).doesNotContain(consultationBack);
        assertThat(consultationBack.getConsultant()).isNull();

        consultant.consultations(new HashSet<>(Set.of(consultationBack)));
        assertThat(consultant.getConsultations()).containsOnly(consultationBack);
        assertThat(consultationBack.getConsultant()).isEqualTo(consultant);

        consultant.setConsultations(new HashSet<>());
        assertThat(consultant.getConsultations()).doesNotContain(consultationBack);
        assertThat(consultationBack.getConsultant()).isNull();
    }
}
