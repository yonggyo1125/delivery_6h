package org.sparta.delivery.payment.application;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.payment.domain.Payment;
import org.sparta.delivery.payment.domain.PaymentId;
import org.sparta.delivery.payment.domain.PaymentRepository;
import org.sparta.delivery.payment.domain.exception.PaymentNotFoundException;
import org.sparta.delivery.payment.domain.service.ApprovePayment;
import org.sparta.delivery.payment.domain.service.CancelPayment;
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
    private final ApprovePayment approvePayment;
    private final CancelPayment cancelPayment;

    // 결제 생성
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID create(UUID orderId) {
        Payment payment = new Payment(orderId, orderProvider);
        return paymentRepository.save(payment).getId().getId();
    }

    // 결제 승인 처리
    @Transactional
    public void approve(UUID paymentId, String paymentKey) {
        Payment payment = getPayment(paymentId);
        payment.approve(paymentKey, approvePayment, cancelPayment);
    }

    // 결제 취소 처리
    @Transactional
    public void cancel(UUID paymentId) {
        Payment payment = getPayment(paymentId);
        payment.cancel(cancelPayment);

    }

    private Payment getPayment(UUID paymentId) {
        return paymentRepository.findById(PaymentId.of(paymentId)).orElseThrow(PaymentNotFoundException::new);
    }
}
