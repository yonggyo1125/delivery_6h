package org.sparta.delivery.payment.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sparta.delivery.global.domain.Price;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@ToString
@Getter
@Table(name="P_PAYMENT")
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Payment {
    @EmbeddedId
    private PaymentId id;

    @Embedded
    private PaymentInfo paymentInfo; // 결제 처리 정보

    @Embedded
    private PaymentOrderInfo paymentOrderInfo; // 결제 상품 정보

    @Column(name="payment_log", columnDefinition = "jsonb")
    private String paymentLog; // 결제로그

    @AttributeOverrides(
            @AttributeOverride(name="value", column = @Column(name="payment_amount"))
    )
    private Price amount; // 결제 금액

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(length=45, updatable = false)
    protected String createdBy;

    @Builder
    public Payment(UUID paymentId, String paymentKey, PaymentStatus status, LocalDateTime requestedAt, LocalDateTime approvedAt, UUID orderId, String orderName, int amount, String paymentLog) {
        this.id = paymentId == null ? PaymentId.of() : PaymentId.of(paymentId);
        this.paymentInfo = new PaymentInfo(paymentKey, status, requestedAt, approvedAt);
        this.paymentOrderInfo = new PaymentOrderInfo(orderId, orderName);

        this.paymentLog = paymentLog;
        this.amount = new Price(amount);
    }
}
