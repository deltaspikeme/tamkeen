package com.tamkeen.backoffice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.tamkeen.backoffice.domain.UserResponse} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserResponseDTO implements Serializable {

    private String id;

    @NotNull
    private Instant responseDate;

    private AnswerDTO answer;

    private QuestionDTO question;

    private UserDTO user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Instant responseDate) {
        this.responseDate = responseDate;
    }

    public AnswerDTO getAnswer() {
        return answer;
    }

    public void setAnswer(AnswerDTO answer) {
        this.answer = answer;
    }

    public QuestionDTO getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDTO question) {
        this.question = question;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserResponseDTO)) {
            return false;
        }

        UserResponseDTO userResponseDTO = (UserResponseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userResponseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserResponseDTO{" +
            "id='" + getId() + "'" +
            ", responseDate='" + getResponseDate() + "'" +
            ", answer=" + getAnswer() +
            ", question=" + getQuestion() +
            ", user=" + getUser() +
            "}";
    }
}
