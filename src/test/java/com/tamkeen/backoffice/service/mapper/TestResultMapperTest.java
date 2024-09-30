package com.tamkeen.backoffice.service.mapper;

import static com.tamkeen.backoffice.domain.TestResultAsserts.*;
import static com.tamkeen.backoffice.domain.TestResultTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestResultMapperTest {

    private TestResultMapper testResultMapper;

    @BeforeEach
    void setUp() {
        testResultMapper = new TestResultMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTestResultSample1();
        var actual = testResultMapper.toEntity(testResultMapper.toDto(expected));
        assertTestResultAllPropertiesEquals(expected, actual);
    }
}
