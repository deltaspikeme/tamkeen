package com.tamkeen.backoffice.service.mapper;

import static com.tamkeen.backoffice.domain.UserResponseAsserts.*;
import static com.tamkeen.backoffice.domain.UserResponseTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserResponseMapperTest {

    private UserResponseMapper userResponseMapper;

    @BeforeEach
    void setUp() {
        userResponseMapper = new UserResponseMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserResponseSample1();
        var actual = userResponseMapper.toEntity(userResponseMapper.toDto(expected));
        assertUserResponseAllPropertiesEquals(expected, actual);
    }
}
