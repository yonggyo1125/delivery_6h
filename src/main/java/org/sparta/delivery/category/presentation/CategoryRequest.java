package org.sparta.delivery.category.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CategoryRequest(
        @NotNull
        UUID id,
        @NotBlank
        String name
) { }
