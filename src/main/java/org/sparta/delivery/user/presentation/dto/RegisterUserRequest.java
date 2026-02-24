package org.sparta.delivery.user.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank
        @Size(min=4)
        String username,

        @NotBlank
        @Size(min=8)
        String password,

        @NotBlank
        String confirmPassword,

        @Email
        @NotBlank
        String email,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        String mobile
) { }
