package com.tamkeen.backoffice.service.dto;

import com.tamkeen.backoffice.domain.enumeration.SubscriptionType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.tamkeen.backoffice.domain.UserSubscription} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserSubscriptionDTO implements Serializable {

    private String id;

    @NotNull
    private SubscriptionType subscriptionType;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private UserDTO user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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
        if (!(o instanceof UserSubscriptionDTO)) {
            return false;
        }

        UserSubscriptionDTO userSubscriptionDTO = (UserSubscriptionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userSubscriptionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserSubscriptionDTO{" +
            "id='" + getId() + "'" +
            ", subscriptionType='" + getSubscriptionType() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
