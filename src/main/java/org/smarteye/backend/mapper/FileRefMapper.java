package org.smarteye.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.smarteye.backend.domain.FileRef;
import org.smarteye.backend.web.dto.FileDtos.FileCreateRequest;
import org.smarteye.backend.web.dto.FileDtos.FileResponse;
import org.smarteye.backend.web.dto.FileDtos.FileUpdateRequest;

@Mapper(componentModel = "spring")
public interface FileRefMapper {

    // Entity -> DTO
    FileResponse toResponse(FileRef entity);

    // DTO -> Entity (create)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    FileRef toEntity(FileCreateRequest dto);

    // Update (patch)
    @org.mapstruct.BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget FileRef entity, FileUpdateRequest dto);
}
