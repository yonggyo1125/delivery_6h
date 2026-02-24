package org.sparta.delivery.user.infrastructure;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.global.infrastructure.keycloak.KeycloakProperties;
import org.sparta.delivery.user.application.GenerateTokenService;
import org.sparta.delivery.user.application.TokenInfo;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakGenerateTokenService implements GenerateTokenService {

    private final KeycloakProperties properties;

    @Override
    public TokenInfo generate(String username, String password) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", properties.getClientId());
        form.add("client_secret", properties.getClientSecret());
        form.add("username", username);
        form.add("password", password);
        form.add("scope", "openid profile email");
        RestClient client = RestClient.create();
        ResponseEntity<TokenInfo> res = client.post()
                .uri(String.format("%s/realms/%s/protocol/openid-connect/token", properties.getServerUrl(), properties.getRealm()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .toEntity(TokenInfo.class);

        return res.getStatusCode().is2xxSuccessful() ? res.getBody() : null;
    }
}