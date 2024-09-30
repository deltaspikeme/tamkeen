package com.tamkeen.backoffice.domain;

import java.util.UUID;

public class TestResultTestSamples {

    public static TestResult getTestResultSample1() {
        return new TestResult().id("id1");
    }

    public static TestResult getTestResultSample2() {
        return new TestResult().id("id2");
    }

    public static TestResult getTestResultRandomSampleGenerator() {
        return new TestResult().id(UUID.randomUUID().toString());
    }
}
