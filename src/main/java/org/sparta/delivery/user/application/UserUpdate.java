package org.sparta.delivery.user.application;

import lombok.Builder;

@Builder
public record UserUpdate(
        String firstName,
        String lastName,
        String email,
        String mobile
) {}
