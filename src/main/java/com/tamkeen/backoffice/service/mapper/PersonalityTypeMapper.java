package com.tamkeen.backoffice.service.mapper;

import com.tamkeen.backoffice.domain.PersonalityType;
import com.tamkeen.backoffice.service.dto.PersonalityTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PersonalityType} and its DTO {@link PersonalityTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface PersonalityTypeMapper extends EntityMapper<PersonalityTypeDTO, PersonalityType> {}
