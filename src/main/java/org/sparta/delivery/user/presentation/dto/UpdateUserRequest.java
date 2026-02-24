package org.sparta.delivery.user.presentation.dto;

import jakarta.validation.constraints.Email;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        @Email
        String email,
        String mobile
) {}
