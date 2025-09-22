package org.smarteye.backend.mapper;

import org.mapstruct.*;
import org.smarteye.backend.domain.Device;
import org.smarteye.backend.domain.FileRef;
import org.smarteye.backend.domain.Measurement;
import org.smarteye.backend.domain.Recipe;
import org.smarteye.backend.domain.TechPallet;
import org.smarteye.backend.web.dto.MeasurementDtos.MeasurementCreateRequest;
import org.smarteye.backend.web.dto.MeasurementDtos.MeasurementResponse;

@Mapper(componentModel = "spring")
public interface MeasurementMapper {

    // ===== Entity -> DTO =====
    @Mapping(target = "techPalletId", source = "techPallet.id")
    @Mapping(target = "recipeId", source = "recipe.id")
    @Mapping(target = "deviceId", source = "device.id")
    @Mapping(target = "fileId", source = "file.id")
    MeasurementResponse toResponse(Measurement entity);

    // ===== DTO -> Entity (create) =====
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "techPallet", source = "techPalletId")
    @Mapping(target = "recipe", source = "recipeId")
    @Mapping(target = "device", source = "deviceId")
    @Mapping(target = "file", ignore = true)
    @Mapping(target = "status", ignore = true)         // выставляется бизнес-логикой (CREATED/IN_PROGRESS)
    @Mapping(target = "issueCode", ignore = true)
    @Mapping(target = "massKg", ignore = true)
    @Mapping(target = "summaryMetrics", ignore = true)
    @Mapping(target = "startedAt", ignore = true)      // ставится при старте
    @Mapping(target = "finishedAt", ignore = true)     // ставится при финише
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Measurement toEntity(MeasurementCreateRequest dto);

    // ===== helpers by id =====
    default TechPallet mapTechPallet(Long id) {
        if (id == null) return null;
        TechPallet p = new TechPallet();
        p.setId(id);
        return p;
    }

    default Recipe mapRecipe(Long id) {
        if (id == null) return null;
        Recipe r = new Recipe();
        r.setId(id);
        return r;
    }

    default Device mapDevice(Long id) {
        if (id == null) return null;
        Device d = new Device();
        d.setId(id);
        return d;
    }

    default FileRef mapFile(Long id) {
        if (id == null) return null;
        FileRef f = new FileRef();
        f.setId(id);
        return f;
    }
}
