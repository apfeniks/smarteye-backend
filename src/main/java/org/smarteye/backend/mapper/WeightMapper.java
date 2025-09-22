package org.smarteye.backend.mapper;

import org.mapstruct.*;
import org.smarteye.backend.domain.Device;
import org.smarteye.backend.domain.Measurement;
import org.smarteye.backend.domain.Weight;
import org.smarteye.backend.web.dto.WeightDtos.WeightCreateRequest;
import org.smarteye.backend.web.dto.WeightDtos.WeightResponse;

@Mapper(componentModel = "spring")
public interface WeightMapper {

    // ===== Entity -> DTO =====
    @Mapping(target = "measurementId", source = "measurement.id")
    @Mapping(target = "deviceId", source = "device.id")
    WeightResponse toResponse(Weight entity);

    // ===== DTO -> Entity (create) =====
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "measurement", source = "measurementId")
    @Mapping(target = "device", source = "deviceId")
    @Mapping(target = "createdAt", ignore = true)
    Weight toEntity(WeightCreateRequest dto);

    // ===== helpers by id =====
    default Measurement mapMeasurement(Long id) {
        if (id == null) return null;
        Measurement m = new Measurement();
        m.setId(id);
        return m;
    }

    default Device mapDevice(Long id) {
        if (id == null) return null;
        Device d = new Device();
        d.setId(id);
        return d;
    }
}
