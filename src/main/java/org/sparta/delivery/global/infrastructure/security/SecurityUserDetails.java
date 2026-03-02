package org.sparta.delivery.global.infrastructure.security;

import org.sparta.delivery.global.domain.service.UserDetails;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUserDetails implements UserDetails {
    @Override
    public UUID getId() {
        return null;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getEmail() {
        return "";
    }

    @Override
    public String getMobile() {
        return "";
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }
}
