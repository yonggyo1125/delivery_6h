package org.sparta.delivery.payment.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sparta.delivery.global.domain.BaseUserEntity;
import org.sparta.delivery.global.domain.Price;
import org.sparta.delivery.global.infrastructure.event.Events;
import org.sparta.delivery.payment.domain.event.PaymentApprovedEvent;
import org.sparta.delivery.payment.domain.event.PaymentCancelledEvent;
import org.sparta.delivery.payment.domain.exception.InvalidPaymentException;
import org.sparta.delivery.payment.domain.exception.PaymentAmountMismatchException;
import org.sparta.delivery.payment.domain.exception.PaymentCancelFailureException;
import org.sparta.delivery.payment.domain.service.CancelPayment;
import org.sparta.delivery.payment.domain.service.CancelResult;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 결제 진행 절차
 * 1. 주문서가 주문 접수 상태로 변경되면 OrderAcceptedEvent 이벤트 발생
 * 2. OrderAcceptedEvent 이벤트를 처리하는 핸들러에서 결제 등록(DB에 하나 저장)
 * 3. 프론트엔드에서 orderId(주문번호), OrderName(주문상품), amount(결제금액)으로 결제 진행
 * 4. 성공 콜백으로 백엔드 엔드포인트로 paymentKey,orderId, amount 값이 넘어옴
 * 5. 백엔드 앤드포인트에서는 승인 처리를 하고 성공시 approve 처리, 실패시 abort 처리
 *      - 승인시간, paymentKey, status, paymentLog 등을 업데이트 합니다.
 * 6. 결제가 승인되면 주문서는 입금확인 단계로 업데이트 합니다(승인 후속 처리 - 이벤트 발생)
 *
 */
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
    private LocalDateTime requestedAt; // 결제 요청일시
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
    public Payment(UUID orderId, String orderName, int amount) {
        this.id = PaymentId.of();
        this.status = PaymentStatus.READY; // 결제 생성 초기 상태
        this.paymentOrderInfo = new PaymentOrderInfo(orderId, orderName);
        this.requestedAt = LocalDateTime.now();
        this.amount = new Price(amount);
    }

    // 결제 승인 완료 처리
    public void approve(String key, PaymentStatus status, LocalDateTime approvedAt, String paymentLog, int approvedAmount) {
        // 이미 승인된 경우라면 처리하지 않음
        if (this.status == PaymentStatus.DONE) {
            return;
        }

        // READY 또는 IN_PROGRESS 상태에서만 승인 가능
        if (this.status != PaymentStatus.READY && this.status != PaymentStatus.IN_PROGRESS) {
            throw new InvalidPaymentException("결제 승인이 가능한 상태가 아닙니다.");
        }

        if (key == null || key.isBlank()) {
            throw new InvalidPaymentException("결제 키(paymentKey)는 필수입니다.");
        }

        // 실결제 금액과 최초 등록 금액과 일치하는지 검증(위변조 방지), 검증 실패시 결제된 금액 취소
        if (this.amount.getValue() != approvedAmount) {
            throw new PaymentAmountMismatchException(amount.getValue(), approvedAmount);
        }

        this.key = key;
        this.paymentLog = paymentLog;
        this.status = status;
        this.approvedAt = approvedAt != null ? approvedAt : LocalDateTime.now();

        // 결제가 승인되면 주문상태를 변경하기 위한 후속 처리
        Events.trigger(new PaymentApprovedEvent(paymentOrderInfo.getOrderId()));
    }

    /**
     * 결제 취소
     * 1. 입금 확인 단계(PaymentStatus.DONE) 에서만 가능
     * 2. 취소가 완료되면 주문서도 환불 상태로 변경 - 이벤트 발행
     */
    public void cancel(CancelPayment cancelPayment) {
        // 이미 취소된 상태라면 처리하지 않음
        if (this.status == PaymentStatus.CANCELED) {
            return;
        }

        if (this.status != PaymentStatus.DONE) {
            throw new InvalidPaymentException("결제 취소는 결제 완료(DONE) 상태에서만 가능합니다.");
        }
        // 외부 API 호출 및 결과 수신
        CancelResult result = cancelPayment.cancel(id, key);

        // 결제 취소 실패시 사유와 함께 예외 발생시킴
        if (!result.success()) {
            throw new PaymentCancelFailureException(result.reason());
        }

        // 성공시에만 상태 변경 및 로그 업데이트, 후속처리
        this.status = PaymentStatus.CANCELED;
        this.paymentLog = result.paymentLog();

        // 주문 취소후 후속 처리(주문서의 상태를 환불상태로 변경) - 이벤트 발행
        Events.trigger(new PaymentCancelledEvent(paymentOrderInfo.getOrderId()));
    }

    // 결제 실패/취소 처리
    public void abort() {
        this.status = PaymentStatus.ABORTED;
    }
}
