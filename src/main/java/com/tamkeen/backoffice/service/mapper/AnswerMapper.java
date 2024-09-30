package com.tamkeen.backoffice.service.mapper;

import com.tamkeen.backoffice.domain.Answer;
import com.tamkeen.backoffice.domain.Question;
import com.tamkeen.backoffice.service.dto.AnswerDTO;
import com.tamkeen.backoffice.service.dto.QuestionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Answer} and its DTO {@link AnswerDTO}.
 */
@Mapper(componentModel = "spring")
public interface AnswerMapper extends EntityMapper<AnswerDTO, Answer> {
    @Mapping(target = "question", source = "question", qualifiedByName = "questionId")
    AnswerDTO toDto(Answer s);

    @Named("questionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    QuestionDTO toDtoQuestionId(Question question);
}
