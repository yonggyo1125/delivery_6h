package org.sparta.delivery.payment.application;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.payment.domain.Payment;
import org.sparta.delivery.payment.domain.PaymentRepository;
import org.sparta.delivery.payment.domain.service.OrderProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderProvider orderProvider;

    // 결제 생성
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID create(UUID orderId) {
        Payment payment = new Payment(orderId, orderProvider);
        return paymentRepository.save(payment).getId().getId();
    }
}
