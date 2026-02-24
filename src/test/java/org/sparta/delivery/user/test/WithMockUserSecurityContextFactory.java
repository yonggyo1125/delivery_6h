package org.sparta.delivery.user.test;

import org.sparta.delivery.global.infrastructure.keycloak.KeycloakClientRoleConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.Instant;
import java.util.*;

public class WithMockUserSecurityContextFactory implements WithSecurityContextFactory<MockUser> {
    @Override
    public SecurityContext createSecurityContext(MockUser anno) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", anno.subject());
        claims.put("preferred_username", anno.username());
        claims.put("email", anno.email());
        claims.put("name", anno.name());
        claims.put("mobile", anno.mobile());
        claims.put("iss", anno.issuer());
        Map<String, Object> realmAccess = Map.of("roles", Arrays.stream(anno.roles()).map(s -> "ROLE_" + s).toList());
        claims.put("realm_access", realmAccess);

        Instant now = Instant.now();
        Instant issuedAt = anno.issuedAt() > 0 ? Instant.ofEpochSecond(anno.issuedAt()) : now;
        Instant expiresAt = anno.expiresAt() > 0 ? issuedAt.plusSeconds(anno.expiresAt()) : now.plusSeconds(3600);

        Jwt jwt = new Jwt(
                "token-" + UUID.randomUUID(),
                issuedAt,
                expiresAt,
                Map.of("alg", "none"), // headers
                claims
        );

        KeycloakClientRoleConverter conv =  new KeycloakClientRoleConverter();
        Collection<GrantedAuthority> authorities = conv.convert(jwt);

        Authentication authentication = new JwtAuthenticationToken(jwt, authorities, anno.username()); // JWT 전용 Authentication 구현체 만들기

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication); // 로그인 처리

        return context;
    }
}