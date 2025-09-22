package org.smarteye.backend.mapper;

import org.mapstruct.*;
import org.smarteye.backend.domain.Device;
import org.smarteye.backend.web.dto.DeviceDtos.DeviceCreateRequest;
import org.smarteye.backend.web.dto.DeviceDtos.DeviceResponse;
import org.smarteye.backend.web.dto.DeviceDtos.DeviceUpdateRequest;

@Mapper(componentModel = "spring")
public interface DeviceMapper {

    // Entity -> DTO
    DeviceResponse toResponse(Device entity);

    // DTO -> Entity (create)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Device toEntity(DeviceCreateRequest dto);

    // Partial update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Device entity, DeviceUpdateRequest dto);
}
