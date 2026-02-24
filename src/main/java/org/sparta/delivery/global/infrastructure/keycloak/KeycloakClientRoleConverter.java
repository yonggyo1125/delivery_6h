package org.sparta.delivery.global.infrastructure.keycloak;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakClientRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null) return Collections.emptyList();
        Object roles = ((Map<?,?>)realmAccess).get("roles");
        if (!(roles instanceof Collection)) return Collections.emptyList();
        return ((Collection<?>)roles).stream()
                .map(Object::toString)
                .filter(s -> s.startsWith("ROLE_"))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}