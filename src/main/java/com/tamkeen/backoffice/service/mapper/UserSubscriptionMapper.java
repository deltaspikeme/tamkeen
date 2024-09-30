package com.tamkeen.backoffice.service.mapper;

import com.tamkeen.backoffice.domain.User;
import com.tamkeen.backoffice.domain.UserSubscription;
import com.tamkeen.backoffice.service.dto.UserDTO;
import com.tamkeen.backoffice.service.dto.UserSubscriptionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserSubscription} and its DTO {@link UserSubscriptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserSubscriptionMapper extends EntityMapper<UserSubscriptionDTO, UserSubscription> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    UserSubscriptionDTO toDto(UserSubscription s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
