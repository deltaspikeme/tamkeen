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
 * A Consultant.
 */
@Document(collection = "consultant")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "consultant")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Consultant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @NotNull
    @Field("expertise")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String expertise;

    @Field("bio")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String bio;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @Field("email")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String email;

    @Field("phone")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String phone;

    @Field("services_offered")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String servicesOffered;

    @DBRef
    @Field("user")
    private User user;

    @DBRef
    @Field("consultation")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "consultant" }, allowSetters = true)
    private Set<Consultation> consultations = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Consultant id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Consultant name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpertise() {
        return this.expertise;
    }

    public Consultant expertise(String expertise) {
        this.setExpertise(expertise);
        return this;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getBio() {
        return this.bio;
    }

    public Consultant bio(String bio) {
        this.setBio(bio);
        return this;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return this.email;
    }

    public Consultant email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public Consultant phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getServicesOffered() {
        return this.servicesOffered;
    }

    public Consultant servicesOffered(String servicesOffered) {
        this.setServicesOffered(servicesOffered);
        return this;
    }

    public void setServicesOffered(String servicesOffered) {
        this.servicesOffered = servicesOffered;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Consultant user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Consultation> getConsultations() {
        return this.consultations;
    }

    public void setConsultations(Set<Consultation> consultations) {
        if (this.consultations != null) {
            this.consultations.forEach(i -> i.setConsultant(null));
        }
        if (consultations != null) {
            consultations.forEach(i -> i.setConsultant(this));
        }
        this.consultations = consultations;
    }

    public Consultant consultations(Set<Consultation> consultations) {
        this.setConsultations(consultations);
        return this;
    }

    public Consultant addConsultation(Consultation consultation) {
        this.consultations.add(consultation);
        consultation.setConsultant(this);
        return this;
    }

    public Consultant removeConsultation(Consultation consultation) {
        this.consultations.remove(consultation);
        consultation.setConsultant(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Consultant)) {
            return false;
        }
        return getId() != null && getId().equals(((Consultant) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Consultant{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", expertise='" + getExpertise() + "'" +
            ", bio='" + getBio() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", servicesOffered='" + getServicesOffered() + "'" +
            "}";
    }
}
