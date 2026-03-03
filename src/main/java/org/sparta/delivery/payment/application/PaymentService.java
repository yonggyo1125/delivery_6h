package org.sparta.delivery.payment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    // 결제 생성
    private UUID create(UUID orderId) {
        return null;
    }
}
