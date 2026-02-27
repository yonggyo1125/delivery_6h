package org.sparta.delivery.global.infrastructure.security;

import org.sparta.delivery.global.domain.service.RoleCheck;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SecurityRoleCheck implements RoleCheck {

    @Override
    public boolean hasRole(String role) {
        return false;
    }

    @Override
    public boolean hasRole(List<String> roles) {
        return false;
    }
}
