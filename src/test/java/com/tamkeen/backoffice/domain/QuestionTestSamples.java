package com.tamkeen.backoffice.domain;

import java.util.UUID;

public class QuestionTestSamples {

    public static Question getQuestionSample1() {
        return new Question().id("id1");
    }

    public static Question getQuestionSample2() {
        return new Question().id("id2");
    }

    public static Question getQuestionRandomSampleGenerator() {
        return new Question().id(UUID.randomUUID().toString());
    }
}
