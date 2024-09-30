package com.tamkeen.backoffice.domain;

import static com.tamkeen.backoffice.domain.AnswerTestSamples.*;
import static com.tamkeen.backoffice.domain.PersonalityTestTestSamples.*;
import static com.tamkeen.backoffice.domain.QuestionTestSamples.*;
import static com.tamkeen.backoffice.domain.UserResponseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.tamkeen.backoffice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class QuestionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Question.class);
        Question question1 = getQuestionSample1();
        Question question2 = new Question();
        assertThat(question1).isNotEqualTo(question2);

        question2.setId(question1.getId());
        assertThat(question1).isEqualTo(question2);

        question2 = getQuestionSample2();
        assertThat(question1).isNotEqualTo(question2);
    }

    @Test
    void answerTest() {
        Question question = getQuestionRandomSampleGenerator();
        Answer answerBack = getAnswerRandomSampleGenerator();

        question.addAnswer(answerBack);
        assertThat(question.getAnswers()).containsOnly(answerBack);
        assertThat(answerBack.getQuestion()).isEqualTo(question);

        question.removeAnswer(answerBack);
        assertThat(question.getAnswers()).doesNotContain(answerBack);
        assertThat(answerBack.getQuestion()).isNull();

        question.answers(new HashSet<>(Set.of(answerBack)));
        assertThat(question.getAnswers()).containsOnly(answerBack);
        assertThat(answerBack.getQuestion()).isEqualTo(question);

        question.setAnswers(new HashSet<>());
        assertThat(question.getAnswers()).doesNotContain(answerBack);
        assertThat(answerBack.getQuestion()).isNull();
    }

    @Test
    void personalityTestTest() {
        Question question = getQuestionRandomSampleGenerator();
        PersonalityTest personalityTestBack = getPersonalityTestRandomSampleGenerator();

        question.setPersonalityTest(personalityTestBack);
        assertThat(question.getPersonalityTest()).isEqualTo(personalityTestBack);

        question.personalityTest(null);
        assertThat(question.getPersonalityTest()).isNull();
    }

    @Test
    void userResponseTest() {
        Question question = getQuestionRandomSampleGenerator();
        UserResponse userResponseBack = getUserResponseRandomSampleGenerator();

        question.addUserResponse(userResponseBack);
        assertThat(question.getUserResponses()).containsOnly(userResponseBack);
        assertThat(userResponseBack.getQuestion()).isEqualTo(question);

        question.removeUserResponse(userResponseBack);
        assertThat(question.getUserResponses()).doesNotContain(userResponseBack);
        assertThat(userResponseBack.getQuestion()).isNull();

        question.userResponses(new HashSet<>(Set.of(userResponseBack)));
        assertThat(question.getUserResponses()).containsOnly(userResponseBack);
        assertThat(userResponseBack.getQuestion()).isEqualTo(question);

        question.setUserResponses(new HashSet<>());
        assertThat(question.getUserResponses()).doesNotContain(userResponseBack);
        assertThat(userResponseBack.getQuestion()).isNull();
    }
}
