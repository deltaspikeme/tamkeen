package com.tamkeen.backoffice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A TestResult.
 */
@Document(collection = "test_result")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "testresult")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TestResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("analysis")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String analysis;

    @NotNull
    @Field("result_date")
    private Instant resultDate;

    @DBRef
    @Field("user")
    private User user;

    @DBRef
    @Field("personalityTest")
    @JsonIgnoreProperties(value = { "questions", "testResults" }, allowSetters = true)
    private PersonalityTest personalityTest;

    @DBRef
    @Field("personalityType")
    @JsonIgnoreProperties(value = { "testResults" }, allowSetters = true)
    private PersonalityType personalityType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public TestResult id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnalysis() {
        return this.analysis;
    }

    public TestResult analysis(String analysis) {
        this.setAnalysis(analysis);
        return this;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public Instant getResultDate() {
        return this.resultDate;
    }

    public TestResult resultDate(Instant resultDate) {
        this.setResultDate(resultDate);
        return this;
    }

    public void setResultDate(Instant resultDate) {
        this.resultDate = resultDate;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TestResult user(User user) {
        this.setUser(user);
        return this;
    }

    public PersonalityTest getPersonalityTest() {
        return this.personalityTest;
    }

    public void setPersonalityTest(PersonalityTest personalityTest) {
        this.personalityTest = personalityTest;
    }

    public TestResult personalityTest(PersonalityTest personalityTest) {
        this.setPersonalityTest(personalityTest);
        return this;
    }

    public PersonalityType getPersonalityType() {
        return this.personalityType;
    }

    public void setPersonalityType(PersonalityType personalityType) {
        this.personalityType = personalityType;
    }

    public TestResult personalityType(PersonalityType personalityType) {
        this.setPersonalityType(personalityType);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TestResult)) {
            return false;
        }
        return getId() != null && getId().equals(((TestResult) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TestResult{" +
            "id=" + getId() +
            ", analysis='" + getAnalysis() + "'" +
            ", resultDate='" + getResultDate() + "'" +
            "}";
    }
}
