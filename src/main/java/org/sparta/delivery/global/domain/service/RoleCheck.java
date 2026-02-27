package org.sparta.delivery.global.domain.service;

import java.util.List;

public interface RoleCheck {
    boolean hasRole(String role);
    boolean hasRole(List<String> roles);
}
