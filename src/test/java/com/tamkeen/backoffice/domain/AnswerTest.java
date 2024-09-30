package com.tamkeen.backoffice.domain;

import static com.tamkeen.backoffice.domain.AnswerTestSamples.*;
import static com.tamkeen.backoffice.domain.QuestionTestSamples.*;
import static com.tamkeen.backoffice.domain.UserResponseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.tamkeen.backoffice.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AnswerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Answer.class);
        Answer answer1 = getAnswerSample1();
        Answer answer2 = new Answer();
        assertThat(answer1).isNotEqualTo(answer2);

        answer2.setId(answer1.getId());
        assertThat(answer1).isEqualTo(answer2);

        answer2 = getAnswerSample2();
        assertThat(answer1).isNotEqualTo(answer2);
    }

    @Test
    void questionTest() {
        Answer answer = getAnswerRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        answer.setQuestion(questionBack);
        assertThat(answer.getQuestion()).isEqualTo(questionBack);

        answer.question(null);
        assertThat(answer.getQuestion()).isNull();
    }

    @Test
    void userResponseTest() {
        Answer answer = getAnswerRandomSampleGenerator();
        UserResponse userResponseBack = getUserResponseRandomSampleGenerator();

        answer.addUserResponse(userResponseBack);
        assertThat(answer.getUserResponses()).containsOnly(userResponseBack);
        assertThat(userResponseBack.getAnswer()).isEqualTo(answer);

        answer.removeUserResponse(userResponseBack);
        assertThat(answer.getUserResponses()).doesNotContain(userResponseBack);
        assertThat(userResponseBack.getAnswer()).isNull();

        answer.userResponses(new HashSet<>(Set.of(userResponseBack)));
        assertThat(answer.getUserResponses()).containsOnly(userResponseBack);
        assertThat(userResponseBack.getAnswer()).isEqualTo(answer);

        answer.setUserResponses(new HashSet<>());
        assertThat(answer.getUserResponses()).doesNotContain(userResponseBack);
        assertThat(userResponseBack.getAnswer()).isNull();
    }
}
