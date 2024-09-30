package com.tamkeen.backoffice.domain;

import static com.tamkeen.backoffice.domain.PersonalityTestTestSamples.*;
import static com.tamkeen.backoffice.domain.QuestionTestSamples.*;
import static com.tamkeen.backoffice.domain.TestResultTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.tamkeen.backoffice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PersonalityTestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PersonalityTest.class);
        PersonalityTest personalityTest1 = getPersonalityTestSample1();
        PersonalityTest personalityTest2 = new PersonalityTest();
        assertThat(personalityTest1).isNotEqualTo(personalityTest2);

        personalityTest2.setId(personalityTest1.getId());
        assertThat(personalityTest1).isEqualTo(personalityTest2);

        personalityTest2 = getPersonalityTestSample2();
        assertThat(personalityTest1).isNotEqualTo(personalityTest2);
    }

    @Test
    void questionTest() {
        PersonalityTest personalityTest = getPersonalityTestRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        personalityTest.addQuestion(questionBack);
        assertThat(personalityTest.getQuestions()).containsOnly(questionBack);
        assertThat(questionBack.getPersonalityTest()).isEqualTo(personalityTest);

        personalityTest.removeQuestion(questionBack);
        assertThat(personalityTest.getQuestions()).doesNotContain(questionBack);
        assertThat(questionBack.getPersonalityTest()).isNull();

        personalityTest.questions(new HashSet<>(Set.of(questionBack)));
        assertThat(personalityTest.getQuestions()).containsOnly(questionBack);
        assertThat(questionBack.getPersonalityTest()).isEqualTo(personalityTest);

        personalityTest.setQuestions(new HashSet<>());
        assertThat(personalityTest.getQuestions()).doesNotContain(questionBack);
        assertThat(questionBack.getPersonalityTest()).isNull();
    }

    @Test
    void testResultTest() {
        PersonalityTest personalityTest = getPersonalityTestRandomSampleGenerator();
        TestResult testResultBack = getTestResultRandomSampleGenerator();

        personalityTest.addTestResult(testResultBack);
        assertThat(personalityTest.getTestResults()).containsOnly(testResultBack);
        assertThat(testResultBack.getPersonalityTest()).isEqualTo(personalityTest);

        personalityTest.removeTestResult(testResultBack);
        assertThat(personalityTest.getTestResults()).doesNotContain(testResultBack);
        assertThat(testResultBack.getPersonalityTest()).isNull();

        personalityTest.testResults(new HashSet<>(Set.of(testResultBack)));
        assertThat(personalityTest.getTestResults()).containsOnly(testResultBack);
        assertThat(testResultBack.getPersonalityTest()).isEqualTo(personalityTest);

        personalityTest.setTestResults(new HashSet<>());
        assertThat(personalityTest.getTestResults()).doesNotContain(testResultBack);
        assertThat(testResultBack.getPersonalityTest()).isNull();
    }
}
