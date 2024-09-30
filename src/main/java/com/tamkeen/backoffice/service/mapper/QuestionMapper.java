package com.tamkeen.backoffice.service.mapper;

import com.tamkeen.backoffice.domain.PersonalityTest;
import com.tamkeen.backoffice.domain.Question;
import com.tamkeen.backoffice.service.dto.PersonalityTestDTO;
import com.tamkeen.backoffice.service.dto.QuestionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Question} and its DTO {@link QuestionDTO}.
 */
@Mapper(componentModel = "spring")
public interface QuestionMapper extends EntityMapper<QuestionDTO, Question> {
    QuestionDTO toDto(Question s);

    @Named("personalityTestId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PersonalityTestDTO toDtoPersonalityTestId(PersonalityTest personalityTest);
}
