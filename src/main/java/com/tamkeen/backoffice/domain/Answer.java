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
 * A Answer.
 */
@Document(collection = "answer")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "answer")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Answer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("answer_text")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String answerText;

    @NotNull
    @Field("score")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer score;

    @DBRef
    @Field("question")
    @JsonIgnoreProperties(value = { "answers", "personalityTest", "userResponses" }, allowSetters = true)
    private Question question;

    @DBRef
    @Field("userResponse")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "answer", "question", "user" }, allowSetters = true)
    private Set<UserResponse> userResponses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Answer id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnswerText() {
        return this.answerText;
    }

    public Answer answerText(String answerText) {
        this.setAnswerText(answerText);
        return this;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Integer getScore() {
        return this.score;
    }

    public Answer score(Integer score) {
        this.setScore(score);
        return this;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Question getQuestion() {
        return this.question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Answer question(Question question) {
        this.setQuestion(question);
        return this;
    }

    public Set<UserResponse> getUserResponses() {
        return this.userResponses;
    }

    public void setUserResponses(Set<UserResponse> userResponses) {
        if (this.userResponses != null) {
            this.userResponses.forEach(i -> i.setAnswer(null));
        }
        if (userResponses != null) {
            userResponses.forEach(i -> i.setAnswer(this));
        }
        this.userResponses = userResponses;
    }

    public Answer userResponses(Set<UserResponse> userResponses) {
        this.setUserResponses(userResponses);
        return this;
    }

    public Answer addUserResponse(UserResponse userResponse) {
        this.userResponses.add(userResponse);
        userResponse.setAnswer(this);
        return this;
    }

    public Answer removeUserResponse(UserResponse userResponse) {
        this.userResponses.remove(userResponse);
        userResponse.setAnswer(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Answer)) {
            return false;
        }
        return getId() != null && getId().equals(((Answer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Answer{" +
            "id=" + getId() +
            ", answerText='" + getAnswerText() + "'" +
            ", score=" + getScore() +
            "}";
    }
}
