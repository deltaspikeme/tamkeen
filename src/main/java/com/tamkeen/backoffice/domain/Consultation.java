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
 * A Consultation.
 */
@Document(collection = "consultation")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "consultation")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Consultation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("consultant_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String consultantName;

    @NotNull
    @Field("consultation_date")
    private Instant consultationDate;

    @Field("notes")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String notes;

    @DBRef
    @Field("consultant")
    @JsonIgnoreProperties(value = { "user", "consultations" }, allowSetters = true)
    private Consultant consultant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Consultation id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConsultantName() {
        return this.consultantName;
    }

    public Consultation consultantName(String consultantName) {
        this.setConsultantName(consultantName);
        return this;
    }

    public void setConsultantName(String consultantName) {
        this.consultantName = consultantName;
    }

    public Instant getConsultationDate() {
        return this.consultationDate;
    }

    public Consultation consultationDate(Instant consultationDate) {
        this.setConsultationDate(consultationDate);
        return this;
    }

    public void setConsultationDate(Instant consultationDate) {
        this.consultationDate = consultationDate;
    }

    public String getNotes() {
        return this.notes;
    }

    public Consultation notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Consultant getConsultant() {
        return this.consultant;
    }

    public void setConsultant(Consultant consultant) {
        this.consultant = consultant;
    }

    public Consultation consultant(Consultant consultant) {
        this.setConsultant(consultant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Consultation)) {
            return false;
        }
        return getId() != null && getId().equals(((Consultation) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Consultation{" +
            "id=" + getId() +
            ", consultantName='" + getConsultantName() + "'" +
            ", consultationDate='" + getConsultationDate() + "'" +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
