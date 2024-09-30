package com.tamkeen.backoffice.service.mapper;

import com.tamkeen.backoffice.domain.Consultant;
import com.tamkeen.backoffice.domain.User;
import com.tamkeen.backoffice.service.dto.ConsultantDTO;
import com.tamkeen.backoffice.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Consultant} and its DTO {@link ConsultantDTO}.
 */
@Mapper(componentModel = "spring")
public interface ConsultantMapper extends EntityMapper<ConsultantDTO, Consultant> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    ConsultantDTO toDto(Consultant s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
