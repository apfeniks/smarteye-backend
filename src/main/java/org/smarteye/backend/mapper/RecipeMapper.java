package org.smarteye.backend.mapper;

import org.mapstruct.*;
import org.smarteye.backend.domain.Recipe;
import org.smarteye.backend.web.dto.RecipeDtos.RecipeCreateRequest;
import org.smarteye.backend.web.dto.RecipeDtos.RecipeResponse;
import org.smarteye.backend.web.dto.RecipeDtos.RecipeUpdateRequest;

@Mapper(componentModel = "spring")
public interface RecipeMapper {

    // Entity -> DTO
    RecipeResponse toResponse(Recipe entity);

    // DTO -> Entity (create)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Recipe toEntity(RecipeCreateRequest dto);

    // Partial update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Recipe entity, RecipeUpdateRequest dto);
}
