package com.tamkeen.backoffice.service.mapper;

import com.tamkeen.backoffice.domain.PersonalityTest;
import com.tamkeen.backoffice.service.dto.PersonalityTestDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PersonalityTest} and its DTO {@link PersonalityTestDTO}.
 */
@Mapper(componentModel = "spring")
public interface PersonalityTestMapper extends EntityMapper<PersonalityTestDTO, PersonalityTest> {}
