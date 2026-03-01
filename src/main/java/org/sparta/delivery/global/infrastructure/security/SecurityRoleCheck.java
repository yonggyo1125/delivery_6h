package org.sparta.delivery.global.infrastructure.security;

import org.sparta.delivery.global.domain.service.RoleCheck;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SecurityRoleCheck implements RoleCheck {

    @Override
    public boolean hasRole(String role) {
        return getAuthorities().contains(ensureRolePrefix(role));
    }

    @Override
    public boolean hasRole(List<String> roles) {
        Collection<String> userAuthorities = getAuthorities();
        return roles.stream()
                .map(this::ensureRolePrefix)
                .anyMatch(userAuthorities::contains);
    }


    // 현재 인증된 사용자의 권한 목록(문자열) 추출
    private Collection<String> getAuthorities() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt)) {
            return List.of();
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet()); // 빠른 검색을 위해 Set으로 변환
    }

    // 입력받은 롤 이름에 ROLE_ 접두사가 없으면 붙여줌
    private String ensureRolePrefix(String role) {
        if (role == null) return "";
        return role.startsWith("ROLE_") ? role : "ROLE_" + role;
    }
}
