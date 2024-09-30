package com.tamkeen.backoffice.service.mapper;

import com.tamkeen.backoffice.domain.PersonalityTest;
import com.tamkeen.backoffice.domain.PersonalityType;
import com.tamkeen.backoffice.domain.TestResult;
import com.tamkeen.backoffice.domain.User;
import com.tamkeen.backoffice.service.dto.PersonalityTestDTO;
import com.tamkeen.backoffice.service.dto.PersonalityTypeDTO;
import com.tamkeen.backoffice.service.dto.TestResultDTO;
import com.tamkeen.backoffice.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TestResult} and its DTO {@link TestResultDTO}.
 */
@Mapper(componentModel = "spring")
public interface TestResultMapper extends EntityMapper<TestResultDTO, TestResult> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "personalityTest", source = "personalityTest", qualifiedByName = "personalityTestId")
    @Mapping(target = "personalityType", source = "personalityType", qualifiedByName = "personalityTypeId")
    TestResultDTO toDto(TestResult s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("personalityTestId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PersonalityTestDTO toDtoPersonalityTestId(PersonalityTest personalityTest);

    @Named("personalityTypeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PersonalityTypeDTO toDtoPersonalityTypeId(PersonalityType personalityType);
}
