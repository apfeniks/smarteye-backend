package org.smarteye.backend.web.dto;

import jakarta.validation.constraints.NotBlank;

public record PresignRequest(
        @NotBlank String objectKey,
        String method  // "PUT" | "GET" (по умолчанию PUT)
) {}
