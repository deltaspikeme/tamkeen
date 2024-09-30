package com.tamkeen.backoffice.service.mapper;

import com.tamkeen.backoffice.domain.Answer;
import com.tamkeen.backoffice.domain.Question;
import com.tamkeen.backoffice.domain.User;
import com.tamkeen.backoffice.domain.UserResponse;
import com.tamkeen.backoffice.service.dto.AnswerDTO;
import com.tamkeen.backoffice.service.dto.QuestionDTO;
import com.tamkeen.backoffice.service.dto.UserDTO;
import com.tamkeen.backoffice.service.dto.UserResponseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserResponse} and its DTO {@link UserResponseDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserResponseMapper extends EntityMapper<UserResponseDTO, UserResponse> {
    @Mapping(target = "answer", source = "answer", qualifiedByName = "answerId")
    @Mapping(target = "question", source = "question", qualifiedByName = "questionId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    UserResponseDTO toDto(UserResponse s);

    @Named("answerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AnswerDTO toDtoAnswerId(Answer answer);

    @Named("questionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    QuestionDTO toDtoQuestionId(Question question);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
