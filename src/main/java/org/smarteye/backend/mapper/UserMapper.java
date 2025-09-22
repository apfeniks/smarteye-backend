package org.smarteye.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.smarteye.backend.domain.User;
import org.smarteye.backend.web.dto.UserDtos.UserCreateRequest;
import org.smarteye.backend.web.dto.UserDtos.UserResponse;
import org.smarteye.backend.web.dto.UserDtos.UserUpdateRequest;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Entity -> DTO
    UserResponse toResponse(User entity);

    // DTO -> Entity (create)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "password", ignore = true) // хэш пароля заполняется в сервисе
    @Mapping(target = "enabled", constant = "true")
    User toEntity(UserCreateRequest dto);

    // Partial update (без смены пароля)
    @org.mapstruct.BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", ignore = true)
    void update(@MappingTarget User entity, UserUpdateRequest dto);
}
