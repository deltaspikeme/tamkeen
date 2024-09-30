package com.tamkeen.backoffice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class AnswerTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Answer getAnswerSample1() {
        return new Answer().id("id1").answerText("answerText1").score(1);
    }

    public static Answer getAnswerSample2() {
        return new Answer().id("id2").answerText("answerText2").score(2);
    }

    public static Answer getAnswerRandomSampleGenerator() {
        return new Answer().id(UUID.randomUUID().toString()).answerText(UUID.randomUUID().toString()).score(intCount.incrementAndGet());
    }
}
