package org.sparta.delivery.global.infrastructure.security;

import org.sparta.delivery.global.domain.service.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class SecurityUserDetails implements UserDetails {

    @Override
    public UUID getId() {
        return getAuthentication()
                .map(auth -> UUID.fromString(auth.getName()))
                .orElse(null);
    }

    @Override
    public String getName() {
        return getAttribute("name");
    }

    @Override
    public String getEmail() {
        return getAttribute("email");
    }

    @Override
    public String getMobile() {
        return getAttribute("mobile");
    }

    @Override
    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated();
    }

    /**
     * 특정 클레임 값을 가져오되, 없으면 null을 반환
     */
    private String getAttribute(String key) {
        return getAttributes()
                .map(attr -> (String) attr.get(key)) // getOrDefault 대신 get 사용 시 없으면 null 반환
                .orElse(null);
    }

    /**
     * Spring Security Context에서 JwtAuthenticationToken 추출
     */
    private Optional<JwtAuthenticationToken> getAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return Optional.of(jwtAuth);
        }
        return Optional.empty();
    }

    /**
     * JWT의 Payload(Claims) Map 추출
     */
    private Optional<Map<String, Object>> getAttributes() {
        return getAuthentication().map(JwtAuthenticationToken::getTokenAttributes);
    }
}