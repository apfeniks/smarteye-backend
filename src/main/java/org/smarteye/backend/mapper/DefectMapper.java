package org.smarteye.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.smarteye.backend.domain.Defect;
import org.smarteye.backend.web.dto.DefectDtos.DefectCreateRequest;
import org.smarteye.backend.web.dto.DefectDtos.DefectResponse;
import org.smarteye.backend.web.dto.DefectDtos.DefectUpdateRequest;

@Mapper(componentModel = "spring")
public interface DefectMapper {

    // Entity -> DTO
    @Mapping(target = "measurementId", source = "measurement.id")
    DefectResponse toResponse(Defect entity);

    // DTO -> Entity (create)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "measurement.id", source = "measurementId")
    @Mapping(target = "createdAt", ignore = true)
    Defect toEntity(DefectCreateRequest dto);

    // Partial update
    @org.mapstruct.BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Defect entity, DefectUpdateRequest dto);
}
