package com.tamkeen.backoffice.service.mapper;

import static com.tamkeen.backoffice.domain.PersonalityTestAsserts.*;
import static com.tamkeen.backoffice.domain.PersonalityTestTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PersonalityTestMapperTest {

    private PersonalityTestMapper personalityTestMapper;

    @BeforeEach
    void setUp() {
        personalityTestMapper = new PersonalityTestMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPersonalityTestSample1();
        var actual = personalityTestMapper.toEntity(personalityTestMapper.toDto(expected));
        assertPersonalityTestAllPropertiesEquals(expected, actual);
    }
}
