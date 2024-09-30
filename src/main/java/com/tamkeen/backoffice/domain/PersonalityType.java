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
 * A PersonalityType.
 */
@Document(collection = "personality_type")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "personalitytype")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PersonalityType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Size(min = 4, max = 4)
    @Field("type_code")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String typeCode;

    @Field("description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @Field("strengths")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String strengths;

    @Field("weaknesses")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String weaknesses;

    @DBRef
    @Field("testResult")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "personalityTest", "personalityType" }, allowSetters = true)
    private Set<TestResult> testResults = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public PersonalityType id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeCode() {
        return this.typeCode;
    }

    public PersonalityType typeCode(String typeCode) {
        this.setTypeCode(typeCode);
        return this;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getDescription() {
        return this.description;
    }

    public PersonalityType description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStrengths() {
        return this.strengths;
    }

    public PersonalityType strengths(String strengths) {
        this.setStrengths(strengths);
        return this;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeaknesses() {
        return this.weaknesses;
    }

    public PersonalityType weaknesses(String weaknesses) {
        this.setWeaknesses(weaknesses);
        return this;
    }

    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }

    public Set<TestResult> getTestResults() {
        return this.testResults;
    }

    public void setTestResults(Set<TestResult> testResults) {
        if (this.testResults != null) {
            this.testResults.forEach(i -> i.setPersonalityType(null));
        }
        if (testResults != null) {
            testResults.forEach(i -> i.setPersonalityType(this));
        }
        this.testResults = testResults;
    }

    public PersonalityType testResults(Set<TestResult> testResults) {
        this.setTestResults(testResults);
        return this;
    }

    public PersonalityType addTestResult(TestResult testResult) {
        this.testResults.add(testResult);
        testResult.setPersonalityType(this);
        return this;
    }

    public PersonalityType removeTestResult(TestResult testResult) {
        this.testResults.remove(testResult);
        testResult.setPersonalityType(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PersonalityType)) {
            return false;
        }
        return getId() != null && getId().equals(((PersonalityType) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PersonalityType{" +
            "id=" + getId() +
            ", typeCode='" + getTypeCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", strengths='" + getStrengths() + "'" +
            ", weaknesses='" + getWeaknesses() + "'" +
            "}";
    }
}
