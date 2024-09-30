package com.tamkeen.backoffice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.tamkeen.backoffice.domain.Consultant} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ConsultantDTO implements Serializable {

    private String id;

    @NotNull
    private String name;

    @NotNull
    private String expertise;

    private String bio;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;

    private String phone;

    private String servicesOffered;

    private UserDTO user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getServicesOffered() {
        return servicesOffered;
    }

    public void setServicesOffered(String servicesOffered) {
        this.servicesOffered = servicesOffered;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConsultantDTO)) {
            return false;
        }

        ConsultantDTO consultantDTO = (ConsultantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, consultantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ConsultantDTO{" +
            "id='" + getId() + "'" +
            ", name='" + getName() + "'" +
            ", expertise='" + getExpertise() + "'" +
            ", bio='" + getBio() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", servicesOffered='" + getServicesOffered() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
