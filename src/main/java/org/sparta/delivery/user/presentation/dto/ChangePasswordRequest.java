package org.sparta.delivery.user.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank
        @Size(min=8)
        String password,

        @NotBlank
        String confirmPassword
) {}
