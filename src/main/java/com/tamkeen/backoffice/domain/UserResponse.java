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
 * A UserResponse.
 */
@Document(collection = "user_response")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "userresponse")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("response_date")
    private Instant responseDate;

    @DBRef
    @Field("answer")
    @JsonIgnoreProperties(value = { "question", "userResponses" }, allowSetters = true)
    private Answer answer;

    @DBRef
    @Field("question")
    @JsonIgnoreProperties(value = { "answers", "personalityTest", "userResponses" }, allowSetters = true)
    private Question question;

    @DBRef
    @Field("user")
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public UserResponse id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getResponseDate() {
        return this.responseDate;
    }

    public UserResponse responseDate(Instant responseDate) {
        this.setResponseDate(responseDate);
        return this;
    }

    public void setResponseDate(Instant responseDate) {
        this.responseDate = responseDate;
    }

    public Answer getAnswer() {
        return this.answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public UserResponse answer(Answer answer) {
        this.setAnswer(answer);
        return this;
    }

    public Question getQuestion() {
        return this.question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public UserResponse question(Question question) {
        this.setQuestion(question);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserResponse user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserResponse)) {
            return false;
        }
        return getId() != null && getId().equals(((UserResponse) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserResponse{" +
            "id=" + getId() +
            ", responseDate='" + getResponseDate() + "'" +
            "}";
    }
}
