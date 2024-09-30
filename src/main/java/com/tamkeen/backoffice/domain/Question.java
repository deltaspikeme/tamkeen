package com.tamkeen.backoffice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tamkeen.backoffice.domain.enumeration.QuestionType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Question.
 */
@Document(collection = "question")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "question")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("question_text")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String questionText;

    @NotNull
    @Field("question_type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QuestionType questionType;

    @DBRef
    @Field("answer")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "question", "userResponses" }, allowSetters = true)
    private Set<Answer> answers = new HashSet<>();

    @DBRef
    @Field("personalityTest")
    @JsonIgnoreProperties(value = { "questions", "testResults" }, allowSetters = true)
    private PersonalityTest personalityTest;

    @DBRef
    @Field("userResponse")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "answer", "question", "user" }, allowSetters = true)
    private Set<UserResponse> userResponses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Question id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionText() {
        return this.questionText;
    }

    public Question questionText(String questionText) {
        this.setQuestionText(questionText);
        return this;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public QuestionType getQuestionType() {
        return this.questionType;
    }

    public Question questionType(QuestionType questionType) {
        this.setQuestionType(questionType);
        return this;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public Set<Answer> getAnswers() {
        return this.answers;
    }

    public void setAnswers(Set<Answer> answers) {
        if (this.answers != null) {
            this.answers.forEach(i -> i.setQuestion(null));
        }
        if (answers != null) {
            answers.forEach(i -> i.setQuestion(this));
        }
        this.answers = answers;
    }

    public Question answers(Set<Answer> answers) {
        this.setAnswers(answers);
        return this;
    }

    public Question addAnswer(Answer answer) {
        this.answers.add(answer);
        answer.setQuestion(this);
        return this;
    }

    public Question removeAnswer(Answer answer) {
        this.answers.remove(answer);
        answer.setQuestion(null);
        return this;
    }

    public PersonalityTest getPersonalityTest() {
        return this.personalityTest;
    }

    public void setPersonalityTest(PersonalityTest personalityTest) {
        this.personalityTest = personalityTest;
    }

    public Question personalityTest(PersonalityTest personalityTest) {
        this.setPersonalityTest(personalityTest);
        return this;
    }

    public Set<UserResponse> getUserResponses() {
        return this.userResponses;
    }

    public void setUserResponses(Set<UserResponse> userResponses) {
        if (this.userResponses != null) {
            this.userResponses.forEach(i -> i.setQuestion(null));
        }
        if (userResponses != null) {
            userResponses.forEach(i -> i.setQuestion(this));
        }
        this.userResponses = userResponses;
    }

    public Question userResponses(Set<UserResponse> userResponses) {
        this.setUserResponses(userResponses);
        return this;
    }

    public Question addUserResponse(UserResponse userResponse) {
        this.userResponses.add(userResponse);
        userResponse.setQuestion(this);
        return this;
    }

    public Question removeUserResponse(UserResponse userResponse) {
        this.userResponses.remove(userResponse);
        userResponse.setQuestion(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Question)) {
            return false;
        }
        return getId() != null && getId().equals(((Question) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Question{" +
            "id=" + getId() +
            ", questionText='" + getQuestionText() + "'" +
            ", questionType='" + getQuestionType() + "'" +
            "}";
    }
}
