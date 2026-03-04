package org.sparta.delivery.payment.infrastructure.api;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.sparta.delivery.payment.domain.Payment;
import org.sparta.delivery.payment.domain.PaymentId;
import org.sparta.delivery.payment.domain.PaymentRepository;
import org.sparta.delivery.payment.domain.exception.PaymentNotFoundException;
import org.sparta.delivery.payment.domain.service.ApprovePayment;
import org.sparta.delivery.payment.domain.service.ApproveResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.Map;

// 결제 승인 처리
@Component
@RequiredArgsConstructor
public class TossApprovePayment implements ApprovePayment {

    private final TossApiHelper tossApiHelper;
    private final PaymentRepository paymentRepository;

    @Override
    public ApproveResult request(PaymentId paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(PaymentNotFoundException::new);

        RestClient restClient = tossApiHelper.getRestClient();

        /**
         *   멱등키 생성 - 토스는 처음 요청한 날부터 15일간 유효하지만
         *   15일 이내에 다시 결제 재시도를 하지는 않으므로 기간은 고려하지 않고
         *   PaymentId의 UUID를 Base64 인코딩한 문자열로 생성하여 유지한다.
         */
        String idempotencyKey = Base64.getEncoder().encodeToString(paymentId.getId().toString().getBytes());


        ResponseEntity<JsonNode> result = restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("confirm")
                        .build())
                .body(
                     Map.of(
                             "paymentKey", payment.getKey(),
                             "orderId", payment.getPaymentOrderInfo().getOrderId(),
                             "amount", payment.getPaymentOrderInfo().getAmount().getValue()
                     )
                )
                .header("Idempotency-Key", idempotencyKey)
                .retrieve()
                .toEntity(JsonNode.class);

        return null;

    }
}
