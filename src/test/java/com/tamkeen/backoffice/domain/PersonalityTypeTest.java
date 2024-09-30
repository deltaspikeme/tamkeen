package com.tamkeen.backoffice.domain;

import static com.tamkeen.backoffice.domain.PersonalityTypeTestSamples.*;
import static com.tamkeen.backoffice.domain.TestResultTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.tamkeen.backoffice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PersonalityTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PersonalityType.class);
        PersonalityType personalityType1 = getPersonalityTypeSample1();
        PersonalityType personalityType2 = new PersonalityType();
        assertThat(personalityType1).isNotEqualTo(personalityType2);

        personalityType2.setId(personalityType1.getId());
        assertThat(personalityType1).isEqualTo(personalityType2);

        personalityType2 = getPersonalityTypeSample2();
        assertThat(personalityType1).isNotEqualTo(personalityType2);
    }

    @Test
    void testResultTest() {
        PersonalityType personalityType = getPersonalityTypeRandomSampleGenerator();
        TestResult testResultBack = getTestResultRandomSampleGenerator();

        personalityType.addTestResult(testResultBack);
        assertThat(personalityType.getTestResults()).containsOnly(testResultBack);
        assertThat(testResultBack.getPersonalityType()).isEqualTo(personalityType);

        personalityType.removeTestResult(testResultBack);
        assertThat(personalityType.getTestResults()).doesNotContain(testResultBack);
        assertThat(testResultBack.getPersonalityType()).isNull();

        personalityType.testResults(new HashSet<>(Set.of(testResultBack)));
        assertThat(personalityType.getTestResults()).containsOnly(testResultBack);
        assertThat(testResultBack.getPersonalityType()).isEqualTo(personalityType);

        personalityType.setTestResults(new HashSet<>());
        assertThat(personalityType.getTestResults()).doesNotContain(testResultBack);
        assertThat(testResultBack.getPersonalityType()).isNull();
    }
}
