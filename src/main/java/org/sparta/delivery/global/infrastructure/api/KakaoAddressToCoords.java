package org.sparta.delivery.global.infrastructure.api;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.delivery.global.domain.service.AddressToCoords;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoAddressToCoords implements AddressToCoords {
    @Value("${KAKAO_API_KEY}")
    private String apiKey;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://dapi.kakao.com")
            .build();


    @Override
    public double[] convert(String address) {
        if (!StringUtils.hasText(address)) return null;

        try {
            ResponseEntity<JsonNode> res = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/address.json")
                            .queryParam("query", address)
                            .build())
                    .header("Authorization", "KakaoAK " + apiKey)
                    .retrieve()
                    .toEntity(JsonNode.class);

            if (res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
                JsonNode documents = res.getBody().get("documents");

                if (documents != null && !documents.isEmpty()) {
                    JsonNode firstDoc = documents.get(0);

                    double lon = firstDoc.get("x").asDouble(); // 경도
                    double lat = firstDoc.get("y").asDouble(); // 위도

                    log.info("Address: {} -> Coords: {}, {}", address, lat, lon);
                    return new double[]{lat, lon};
                }
            }
        } catch (Exception e) {
            log.error("Kakao API 호출 중 오류 발생: {}", e.getMessage());
        }

        log.warn("주소 변환에 실패했습니다: {}", address);
        return null;
    }
}
