package com.tamkeen.backoffice.domain;

import static com.tamkeen.backoffice.domain.ConsultantTestSamples.*;
import static com.tamkeen.backoffice.domain.ConsultationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.tamkeen.backoffice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ConsultationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Consultation.class);
        Consultation consultation1 = getConsultationSample1();
        Consultation consultation2 = new Consultation();
        assertThat(consultation1).isNotEqualTo(consultation2);

        consultation2.setId(consultation1.getId());
        assertThat(consultation1).isEqualTo(consultation2);

        consultation2 = getConsultationSample2();
        assertThat(consultation1).isNotEqualTo(consultation2);
    }

    @Test
    void consultantTest() {
        Consultation consultation = getConsultationRandomSampleGenerator();
        Consultant consultantBack = getConsultantRandomSampleGenerator();

        consultation.setConsultant(consultantBack);
        assertThat(consultation.getConsultant()).isEqualTo(consultantBack);

        consultation.consultant(null);
        assertThat(consultation.getConsultant()).isNull();
    }
}
