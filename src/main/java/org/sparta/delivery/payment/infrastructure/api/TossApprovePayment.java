package org.sparta.delivery.payment.infrastructure.api;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.delivery.payment.domain.Payment;
import org.sparta.delivery.payment.domain.PaymentId;
import org.sparta.delivery.payment.domain.PaymentRepository;
import org.sparta.delivery.payment.domain.PaymentStatus;
import org.sparta.delivery.payment.domain.exception.PaymentNotFoundException;
import org.sparta.delivery.payment.domain.service.ApprovePayment;
import org.sparta.delivery.payment.domain.service.ApproveResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

// 결제 승인 처리
@Slf4j
@Component
@RequiredArgsConstructor
public class TossApprovePayment implements ApprovePayment {

    private final TossApiHelper tossApiHelper;
    private final PaymentRepository paymentRepository;

    @Override
    public ApproveResult request(PaymentId paymentId, String paymentKey) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(PaymentNotFoundException::new);

        RestClient restClient = tossApiHelper.getRestClient();

        /**
         *   멱등키 생성 - 토스는 처음 요청한 날부터 15일간 유효하지만
         *   15일 이후에 다시 결제 재시도를 하지는 않으므로 기간은 고려하지 않고
         *   PaymentId의 UUID를 Base64 인코딩한 문자열로 생성하여 유지한다.
         */
        String idempotencyKey = paymentId.getId().toString();

        ApproveResult.ApproveResultBuilder builder = ApproveResult
                .builder()
                .key(paymentKey);

        UUID orderId = payment.getPaymentOrderInfo().getOrderId();
        int amount = payment.getPaymentOrderInfo().getAmount().getValue();

        log.info("토스 결제 승인 요청 시작, 주문 ID: {}, 결제 ID: {}, 멱등성 키: {}, Payment Key: {}, 결제금액: {}", orderId, paymentId.getId(), idempotencyKey, paymentKey, amount);

        try {
            ResponseEntity<JsonNode> res = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("confirm")
                            .build())
                    .body(
                            Map.of(
                                    "paymentKey", paymentKey,
                                    "orderId", orderId,
                                    "amount", amount
                            )
                    )
                    .header("Idempotency-Key", idempotencyKey)
                    .retrieve()
                    .toEntity(JsonNode.class);

            JsonNode result = res.getBody(); // 토스가 응답한 데이터
            JsonNode statusNode = result.get("status");
            PaymentStatus status = PaymentStatus.valueOf(statusNode.asText());
            LocalDateTime approvedAt = result.get("approvedAt") == null ? null : LocalDateTime.parse(result.get("approvedAt").asText(), DateTimeFormatter.ISO_DATE_TIME);
            int approvedAmount = result.get("totalAmount") == null ? 0 : result.get("totalAmount").asInt(0);

            log.info("토스 결제 승인 성공, 주문 ID: {}, 결제 ID: {}, 멱등성 키: {}, Payment Key: {}, 결제금액: {}, 승인 상태: {}, 승인시간: {}", orderId, paymentId.getId(), idempotencyKey, paymentKey, amount, status, approvedAt);

            return builder
                    .success(true)
                    .status(status)
                    .approvedAt(approvedAt)
                    .approvedAmount(approvedAmount)
                    .paymentLog(result.toString())
                    .build();
        } catch (RestClientResponseException e) {
           JsonNode result = e.getResponseBodyAs(JsonNode.class);
            String code = result == null || result.get("code") == null ? "UNKNOWN":result.get("code").asText();
            String message = result == null || result.get("message") == null ? "UNKNOWN":result.get("message").asText();

            log.info("토스 결제 승인 실패, HTTP 상태코드: {}, 주문 ID: {}, 결제 ID: {}, 멱등성 키: {}, Payment Key: {}, 결제금액: {}, 에러코드: {}, 에러메세지: {}", e.getStatusCode().value(), orderId, paymentId.getId(), idempotencyKey, paymentKey, amount, code, message);

            return builder
                    .success(false)
                    .reason("[%s]%s".formatted(code, message))
                    .paymentLog(result == null ? null : result.toString())
                    .build();
        } catch (Exception e) {
            // 네트워크 타임아웃 또는 기타 예외
            log.info("토스 결제 승인 실패, 주문 ID: {}, 결제 ID: {}, 멱등성 키: {}, Payment Key: {}, 결제금액: {}, 에러코드: UNKNOWN, 에러메세지: {}", orderId, paymentId.getId(), idempotencyKey, paymentKey, amount, e.getMessage());
            return ApproveResult.builder()
                    .success(false)
                    .reason("[UNKNOWN]" + e.getMessage())
                    .build();

        }
    }
}
