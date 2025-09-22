package org.smarteye.backend.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.smarteye.backend.security.model.Role;

import java.time.OffsetDateTime;

public final class UserDtos {

    // ===== CREATE =====
    public record UserCreateRequest(
            @NotBlank @Size(max = 64) String username,
            @NotBlank @Size(min = 6, max = 128) String password,
            Role role                       // если null -> OPERATOR (в сервисе)
    ) {}

    // ===== UPDATE (PATCH) =====
    public record UserUpdateRequest(
            @Size(max = 64) String username,
            Role role,
            Boolean enabled
    ) {}

    // ===== CHANGE PASSWORD =====
    public record ChangePasswordRequest(
            @NotBlank @Size(min = 6, max = 128) String newPassword
    ) {}

    // ===== RESPONSE =====
    public record UserResponse(
            Long id,
            String username,
            Role role,
            boolean enabled,
            OffsetDateTime createdAt
    ) {}
}
