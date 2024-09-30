package com.tamkeen.backoffice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.tamkeen.backoffice.domain.TestResult} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TestResultDTO implements Serializable {

    private String id;

    private String analysis;

    @NotNull
    private Instant resultDate;

    private UserDTO user;

    private PersonalityTestDTO personalityTest;

    private PersonalityTypeDTO personalityType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public Instant getResultDate() {
        return resultDate;
    }

    public void setResultDate(Instant resultDate) {
        this.resultDate = resultDate;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public PersonalityTestDTO getPersonalityTest() {
        return personalityTest;
    }

    public void setPersonalityTest(PersonalityTestDTO personalityTest) {
        this.personalityTest = personalityTest;
    }

    public PersonalityTypeDTO getPersonalityType() {
        return personalityType;
    }

    public void setPersonalityType(PersonalityTypeDTO personalityType) {
        this.personalityType = personalityType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TestResultDTO)) {
            return false;
        }

        TestResultDTO testResultDTO = (TestResultDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, testResultDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TestResultDTO{" +
            "id='" + getId() + "'" +
            ", analysis='" + getAnalysis() + "'" +
            ", resultDate='" + getResultDate() + "'" +
            ", user=" + getUser() +
            ", personalityTest=" + getPersonalityTest() +
            ", personalityType=" + getPersonalityType() +
            "}";
    }
}
