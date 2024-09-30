package com.tamkeen.backoffice.service.mapper;

import static com.tamkeen.backoffice.domain.UserSubscriptionAsserts.*;
import static com.tamkeen.backoffice.domain.UserSubscriptionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserSubscriptionMapperTest {

    private UserSubscriptionMapper userSubscriptionMapper;

    @BeforeEach
    void setUp() {
        userSubscriptionMapper = new UserSubscriptionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserSubscriptionSample1();
        var actual = userSubscriptionMapper.toEntity(userSubscriptionMapper.toDto(expected));
        assertUserSubscriptionAllPropertiesEquals(expected, actual);
    }
}
