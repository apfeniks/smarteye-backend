package org.smarteye.backend.security.api.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;

public final class AuthDtos {

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    public record TokenResponse(
            String token,
            OffsetDateTime expiresAt
    ) {}
}
