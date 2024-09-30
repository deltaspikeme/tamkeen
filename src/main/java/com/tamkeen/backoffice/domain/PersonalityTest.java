package com.tamkeen.backoffice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A PersonalityTest.
 */
@Document(collection = "personality_test")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "personalitytest")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PersonalityTest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("test_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String testName;

    @Field("description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @DBRef
    @Field("question")
    @JsonIgnoreProperties(value = { "personalityTest" }, allowSetters = true)
    private Set<Question> questions = new HashSet<>();

    @DBRef
    @Field("testResult")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "personalityTest", "personalityType" }, allowSetters = true)
    private Set<TestResult> testResults = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public PersonalityTest id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTestName() {
        return this.testName;
    }

    public PersonalityTest testName(String testName) {
        this.setTestName(testName);
        return this;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getDescription() {
        return this.description;
    }

    public PersonalityTest description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Question> getQuestions() {
        return this.questions;
    }

    public void setQuestions(Set<Question> questions) {
        if (this.questions != null) {
            this.questions.forEach(i -> i.setPersonalityTest(null));
        }
        if (questions != null) {
            questions.forEach(i -> i.setPersonalityTest(this));
        }
        this.questions = questions;
    }

    public PersonalityTest questions(Set<Question> questions) {
        this.setQuestions(questions);
        return this;
    }

    public PersonalityTest addQuestion(Question question) {
        this.questions.add(question);
        question.setPersonalityTest(this);
        return this;
    }

    public PersonalityTest removeQuestion(Question question) {
        this.questions.remove(question);
        question.setPersonalityTest(null);
        return this;
    }

    public Set<TestResult> getTestResults() {
        return this.testResults;
    }

    public void setTestResults(Set<TestResult> testResults) {
        if (this.testResults != null) {
            this.testResults.forEach(i -> i.setPersonalityTest(null));
        }
        if (testResults != null) {
            testResults.forEach(i -> i.setPersonalityTest(this));
        }
        this.testResults = testResults;
    }

    public PersonalityTest testResults(Set<TestResult> testResults) {
        this.setTestResults(testResults);
        return this;
    }

    public PersonalityTest addTestResult(TestResult testResult) {
        this.testResults.add(testResult);
        testResult.setPersonalityTest(this);
        return this;
    }

    public PersonalityTest removeTestResult(TestResult testResult) {
        this.testResults.remove(testResult);
        testResult.setPersonalityTest(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PersonalityTest)) {
            return false;
        }
        return getId() != null && getId().equals(((PersonalityTest) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PersonalityTest{" +
            "id=" + getId() +
            ", testName='" + getTestName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
