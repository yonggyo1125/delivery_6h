package org.sparta.delivery.payment.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sparta.delivery.global.domain.BaseUserEntity;
import org.sparta.delivery.global.domain.Price;
import org.sparta.delivery.payment.domain.exception.InvalidPaymentException;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@ToString
@Getter
@Table(name="P_PAYMENT")
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseUserEntity {
    @EmbeddedId
    private PaymentId id;

    @Column(length=45, name="payment_key")
    private String key;

    @Column(length=30, nullable = false, name="payment_status")
    private PaymentStatus status;

    @Column(nullable = false)
    public LocalDateTime requestedAt; // 결제 요청일시
    private LocalDateTime approvedAt; // 결제 승인일시

    @Embedded
    private PaymentOrderInfo paymentOrderInfo; // 결제 상품 정보

    @Column(name="payment_log", columnDefinition = "jsonb")
    private String paymentLog; // 결제로그

    @AttributeOverrides(
            @AttributeOverride(name="value", column = @Column(name="payment_amount"))
    )
    private Price amount; // 결제 금액

    @Builder
    public Payment(UUID paymentId,  PaymentStatus status, LocalDateTime requestedAt, UUID orderId, String orderName, int amount, String paymentLog) {
        this.id = paymentId == null ? PaymentId.of() : PaymentId.of(paymentId);
        this.status = status;

        this.paymentOrderInfo = new PaymentOrderInfo(orderId, orderName);

        this.requestedAt = requestedAt != null ? requestedAt : LocalDateTime.now();
        this.paymentLog = paymentLog;
        this.amount = new Price(amount);
    }

    // 결제 승인 완료 처리
    public void approve(String key, LocalDateTime approvedAt, String paymentLog) {
        // READY 또는 IN_PROGRESS 상태에서만 승인 가능
        if (this.status != PaymentStatus.READY && this.status != PaymentStatus.IN_PROGRESS) {
            throw new InvalidPaymentException("결제 승인이 가능한 상태가 아닙니다.");
        }

        if (key == null || key.isBlank()) {
            throw new InvalidPaymentException("결제 키(paymentKey)는 필수입니다.");
        }

        this.key = key;
        this.paymentLog = paymentLog;
        this.status = PaymentStatus.DONE;
        this.approvedAt = approvedAt != null ? approvedAt : LocalDateTime.now();
    }

    // 결제 실패/취소 처리
    public void abort() {
        this.status = PaymentStatus.ABORTED;
    }
}
