package com.tamkeen.backoffice.service.mapper;

import com.tamkeen.backoffice.domain.Consultant;
import com.tamkeen.backoffice.domain.Consultation;
import com.tamkeen.backoffice.service.dto.ConsultantDTO;
import com.tamkeen.backoffice.service.dto.ConsultationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Consultation} and its DTO {@link ConsultationDTO}.
 */
@Mapper(componentModel = "spring")
public interface ConsultationMapper extends EntityMapper<ConsultationDTO, Consultation> {
    @Mapping(target = "consultant", source = "consultant", qualifiedByName = "consultantId")
    ConsultationDTO toDto(Consultation s);

    @Named("consultantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ConsultantDTO toDtoConsultantId(Consultant consultant);
}
