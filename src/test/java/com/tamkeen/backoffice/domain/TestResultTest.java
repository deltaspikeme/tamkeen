package com.tamkeen.backoffice.domain;

import static com.tamkeen.backoffice.domain.PersonalityTestTestSamples.*;
import static com.tamkeen.backoffice.domain.PersonalityTypeTestSamples.*;
import static com.tamkeen.backoffice.domain.TestResultTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.tamkeen.backoffice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TestResultTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TestResult.class);
        TestResult testResult1 = getTestResultSample1();
        TestResult testResult2 = new TestResult();
        assertThat(testResult1).isNotEqualTo(testResult2);

        testResult2.setId(testResult1.getId());
        assertThat(testResult1).isEqualTo(testResult2);

        testResult2 = getTestResultSample2();
        assertThat(testResult1).isNotEqualTo(testResult2);
    }

    @Test
    void personalityTestTest() {
        TestResult testResult = getTestResultRandomSampleGenerator();
        PersonalityTest personalityTestBack = getPersonalityTestRandomSampleGenerator();

        testResult.setPersonalityTest(personalityTestBack);
        assertThat(testResult.getPersonalityTest()).isEqualTo(personalityTestBack);

        testResult.personalityTest(null);
        assertThat(testResult.getPersonalityTest()).isNull();
    }

    @Test
    void personalityTypeTest() {
        TestResult testResult = getTestResultRandomSampleGenerator();
        PersonalityType personalityTypeBack = getPersonalityTypeRandomSampleGenerator();

        testResult.setPersonalityType(personalityTypeBack);
        assertThat(testResult.getPersonalityType()).isEqualTo(personalityTypeBack);

        testResult.personalityType(null);
        assertThat(testResult.getPersonalityType()).isNull();
    }
}
