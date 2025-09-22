package org.smarteye.backend.mapper;

import org.mapstruct.*;
import org.smarteye.backend.domain.Recipe;
import org.smarteye.backend.domain.TechPallet;
import org.smarteye.backend.web.dto.TechPalletDtos.TechPalletCreateRequest;
import org.smarteye.backend.web.dto.TechPalletDtos.TechPalletResponse;
import org.smarteye.backend.web.dto.TechPalletDtos.TechPalletUpdateRequest;

@Mapper(componentModel = "spring")
public interface TechPalletMapper {

    // ===== Entity -> DTO =====
    @Mapping(target = "recipeId", source = "recipe.id")
    @Mapping(target = "previousTechPalletId", source = "previousTechPallet.id")
    TechPalletResponse toResponse(TechPallet entity);

    // ===== DTO -> Entity (create) =====
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipe", source = "recipeId")
    @Mapping(target = "previousTechPallet", ignore = true) // создаётся бизнес-логикой при реюзе RFID
    @Mapping(target = "createdAt", ignore = true)
    TechPallet toEntity(TechPalletCreateRequest dto);

    // ===== Update (patch) =====
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "recipe", source = "recipeId")
    @Mapping(target = "previousTechPallet", ignore = true)
    void update(@MappingTarget TechPallet entity, TechPalletUpdateRequest dto);

    // ===== helpers =====
    default Recipe mapRecipe(Long id) {
        if (id == null) return null;
        Recipe r = new Recipe();
        r.setId(id);
        return r;
    }
}
