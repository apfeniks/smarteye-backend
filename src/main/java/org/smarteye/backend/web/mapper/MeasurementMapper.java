package org.smarteye.backend.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.smarteye.backend.domain.Measurement;
import org.smarteye.backend.web.dto.MeasurementDto;

@Mapper(componentModel = "spring")
public interface MeasurementMapper {
    @Mapping(target="techPalletId", source="techPallet.id")
    @Mapping(target="pointcloudFileId", source="pointcloudFile.id")
    MeasurementDto toDto(Measurement e);
}
