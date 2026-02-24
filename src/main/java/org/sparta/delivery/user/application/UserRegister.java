package org.sparta.delivery.user.application;

import lombok.Builder;

@Builder
public record UserRegister(
        String username,
        String password,
        String email,
        String firstName,
        String lastName,
        String mobile
) {}