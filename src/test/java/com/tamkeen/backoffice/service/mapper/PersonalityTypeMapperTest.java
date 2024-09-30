package com.tamkeen.backoffice.service.mapper;

import static com.tamkeen.backoffice.domain.PersonalityTypeAsserts.*;
import static com.tamkeen.backoffice.domain.PersonalityTypeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PersonalityTypeMapperTest {

    private PersonalityTypeMapper personalityTypeMapper;

    @BeforeEach
    void setUp() {
        personalityTypeMapper = new PersonalityTypeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPersonalityTypeSample1();
        var actual = personalityTypeMapper.toEntity(personalityTypeMapper.toDto(expected));
        assertPersonalityTypeAllPropertiesEquals(expected, actual);
    }
}
