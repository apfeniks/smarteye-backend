package org.smarteye.backend.web.mapper;

import org.mapstruct.*;
import org.smarteye.backend.domain.TechPallet;
import org.smarteye.backend.web.dto.TechPalletDto;

@Mapper(componentModel = "spring")
public interface TechPalletMapper {
    TechPalletDto toDto(TechPallet entity);
}
