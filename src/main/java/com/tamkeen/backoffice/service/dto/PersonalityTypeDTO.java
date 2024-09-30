package com.tamkeen.backoffice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.tamkeen.backoffice.domain.PersonalityType} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PersonalityTypeDTO implements Serializable {

    private String id;

    @NotNull
    @Size(min = 4, max = 4)
    private String typeCode;

    private String description;

    private String strengths;

    private String weaknesses;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PersonalityTypeDTO)) {
            return false;
        }

        PersonalityTypeDTO personalityTypeDTO = (PersonalityTypeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, personalityTypeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PersonalityTypeDTO{" +
            "id='" + getId() + "'" +
            ", typeCode='" + getTypeCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", strengths='" + getStrengths() + "'" +
            ", weaknesses='" + getWeaknesses() + "'" +
            "}";
    }
}
