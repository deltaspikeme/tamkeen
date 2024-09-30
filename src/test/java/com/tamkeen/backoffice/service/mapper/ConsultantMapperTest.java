package com.tamkeen.backoffice.service.mapper;

import static com.tamkeen.backoffice.domain.ConsultantAsserts.*;
import static com.tamkeen.backoffice.domain.ConsultantTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConsultantMapperTest {

    private ConsultantMapper consultantMapper;

    @BeforeEach
    void setUp() {
        consultantMapper = new ConsultantMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getConsultantSample1();
        var actual = consultantMapper.toEntity(consultantMapper.toDto(expected));
        assertConsultantAllPropertiesEquals(expected, actual);
    }
}
