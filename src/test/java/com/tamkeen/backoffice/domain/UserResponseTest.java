package com.tamkeen.backoffice.domain;

import static com.tamkeen.backoffice.domain.AnswerTestSamples.*;
import static com.tamkeen.backoffice.domain.QuestionTestSamples.*;
import static com.tamkeen.backoffice.domain.UserResponseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.tamkeen.backoffice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserResponseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserResponse.class);
        UserResponse userResponse1 = getUserResponseSample1();
        UserResponse userResponse2 = new UserResponse();
        assertThat(userResponse1).isNotEqualTo(userResponse2);

        userResponse2.setId(userResponse1.getId());
        assertThat(userResponse1).isEqualTo(userResponse2);

        userResponse2 = getUserResponseSample2();
        assertThat(userResponse1).isNotEqualTo(userResponse2);
    }

    @Test
    void answerTest() {
        UserResponse userResponse = getUserResponseRandomSampleGenerator();
        Answer answerBack = getAnswerRandomSampleGenerator();

        userResponse.setAnswer(answerBack);
        assertThat(userResponse.getAnswer()).isEqualTo(answerBack);

        userResponse.answer(null);
        assertThat(userResponse.getAnswer()).isNull();
    }

    @Test
    void questionTest() {
        UserResponse userResponse = getUserResponseRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        userResponse.setQuestion(questionBack);
        assertThat(userResponse.getQuestion()).isEqualTo(questionBack);

        userResponse.question(null);
        assertThat(userResponse.getQuestion()).isNull();
    }
}
