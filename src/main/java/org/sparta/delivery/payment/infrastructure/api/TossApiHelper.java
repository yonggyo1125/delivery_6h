package org.sparta.delivery.payment.infrastructure.api;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Base64;

@Getter
@Component
public class TossApiHelper {

    private final RestClient restClient;

    public TossApiHelper(@Value("${TOSS_SECRET_KEY}") String secretKey) {
        String encodedSecretKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

        restClient = RestClient.builder()
                .baseUrl(URI.create("https://api.tosspayments.com/v1/payments"))
                .defaultHeaders((headers) -> { // 요청헤더
                    headers.setBasicAuth(encodedSecretKey);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .build();
    }
}
