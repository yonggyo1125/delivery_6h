package org.sparta.delivery.payment.infrastructure.api;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.delivery.payment.domain.PaymentId;
import org.sparta.delivery.payment.domain.service.CancelPayment;
import org.sparta.delivery.payment.domain.service.CancelResult;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossCancelPayment implements CancelPayment {
    private final TossApiHelper tossApiHelper;

    @Override
    public CancelResult cancel(PaymentId paymentId, String paymentKey, String cancelReason) {


        RestClient restClient = tossApiHelper.getRestClient();
        String idempotencyKey = paymentId.getId().toString() + "-cancel";

        log.info("토스 결제 취소 요청 시작, 결제 ID: {}, 멱등성 키: {}, Payment Key: {}", paymentId.getId(), idempotencyKey, paymentKey);

        try {
           JsonNode result = restClient.post()
                   .uri(uriBuilder -> uriBuilder
                           .path("{paymentKey}/cancel")
                           .build(paymentKey))
                   .header("Idempotency-Key", idempotencyKey)
                   .body(Map.of("cancelReason", cancelReason))
                   .retrieve()
                   .body(JsonNode.class);

           log.info("토스 결제 취소 성공, 결제 ID: {}, 멱등성 키: {}, Payment Key: {}", paymentId.getId(), idempotencyKey, paymentKey);
           return CancelResult.builder()
                   .success(true)
                   .paymentLog(result == null ? null : result.toString())
                   .build();

        } catch (RestClientResponseException e) {
            JsonNode result = e.getResponseBodyAs(JsonNode.class);
            String code = result == null || result.get("code") == null ? "UNKNOWN":result.get("code").asText();
            String message = result == null || result.get("message") == null ? "UNKNOWN":result.get("message").asText();

            log.info("토스 결제 취소 실패, HTTP 상태코드: {}, 결제 ID: {}, 멱등성 키: {}, Payment Key: {}, 에러코드: {}, 에러메세지: {}", e.getStatusCode().value(), paymentId.getId(), idempotencyKey, paymentKey, code, message);

            return CancelResult.builder()
                    .success(false)
                    .reason("[%s]%s".formatted(code, message))
                    .paymentLog(result == null ? null : result.toString())
                    .build();
        } catch (Exception e) {
            // 네트워크 타임아웃 또는 기타 예외
            log.info("토스 결제 취소 실패,  결제 ID: {}, 멱등성 키: {}, Payment Key: {}, 에러코드: UNKNOWN, 에러메세지: {}", paymentId.getId(), idempotencyKey, paymentKey, e.getMessage());
            return CancelResult.builder()
                    .success(false)
                    .reason("[UNKNOWN]" + e.getMessage())
                    .build();
        }
    }
}
