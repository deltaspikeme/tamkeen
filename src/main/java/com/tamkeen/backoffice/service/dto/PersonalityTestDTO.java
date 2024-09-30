package com.tamkeen.backoffice.service.dto;

import com.tamkeen.backoffice.domain.Question;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.tamkeen.backoffice.domain.PersonalityTest} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PersonalityTestDTO implements Serializable {

    private String id;

    @NotNull
    private String testName;

    private String description;
    private Set<QuestionDTO> questions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<QuestionDTO> questions) {
        this.questions = questions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PersonalityTestDTO)) {
            return false;
        }

        PersonalityTestDTO personalityTestDTO = (PersonalityTestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, personalityTestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PersonalityTestDTO{" +
            "id='" + getId() + "'" +
            ", testName='" + getTestName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
