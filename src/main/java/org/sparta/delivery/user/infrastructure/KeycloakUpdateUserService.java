package org.sparta.delivery.user.infrastructure;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.sparta.delivery.global.infrastructure.keycloak.KeycloakProperties;
import org.sparta.delivery.user.application.UpdateUserService;
import org.sparta.delivery.user.application.UserUpdate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakUpdateUserService implements UpdateUserService {
    private final KeycloakProperties properties;
    private final Keycloak keycloak;

    @Override
    public void update(UUID userId, UserUpdate dto) {
        // 현재 사용자 정보 조회
        UserRepresentation user = getUserProfile(userId);

        if (StringUtils.hasText(dto.firstName())) {
            user.setFirstName(dto.firstName());
        }

        if (StringUtils.hasText(dto.lastName())) {
            user.setLastName(dto.lastName());
        }

        if (StringUtils.hasText(dto.email())) {
            user.setEmail(dto.email());
        }

        Map<String, List<String>> attributes = Objects.requireNonNullElseGet(user.getAttributes(), HashMap::new);

        if (StringUtils.hasText(dto.mobile())) {
            attributes.put("mobile", List.of(dto.mobile()));
        }

        user.setAttributes(attributes);

        // 엡데이트 처리
        keycloak.realm(properties.getRealm()).users().get(userId.toString()).update(user);
    }

    // 비밀번호 변경
    @Override
    public void updatePassword(UUID userId, String newPassword) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(newPassword);

        keycloak.realm(properties.getRealm()).users().get(userId.toString()).resetPassword(passwordCred);
    }

    @Override
    public void updateUserRole(UUID userId, List<String> roleNames) {
        String id = userId.toString();
        String realm = properties.getRealm();
        RoleScopeResource resource = keycloak.realm(realm).users().get(id).roles().realmLevel();

        // 기존 Role 제거
        resource.remove(resource.listAll());

        // 새 Role 추가
        List<RoleRepresentation> newRoles = roleNames.stream().map(roleName -> keycloak.realm(realm).roles().get(roleName).toRepresentation()).toList();
        resource.add(newRoles);
    }

    // 사용자 UUID로 키클록 회원정보 조회
    private UserRepresentation getUserProfile(UUID userId) {
        return keycloak.realm(properties.getRealm()).users().get(userId.toString()).toRepresentation();
    }
}
