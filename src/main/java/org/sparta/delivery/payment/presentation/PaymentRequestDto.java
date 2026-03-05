package org.sparta.delivery.payment.presentation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "결제 관련 요청 DTO")
public class PaymentRequestDto {

    // 결제 승인 요청 (Success Callback용)
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "결제 승인 요청 (성공 콜백)")
    public static class Approve {
        @Schema(description = "토스 결제 고유 키", example = "payment_key_sample")
        @NotBlank(message = "결제 키는 필수 입력값입니다.")
        private String paymentKey;

        @Schema(description = "주문(결제) ID", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "주문 ID는 필수 입력값입니다.")
        private UUID orderId;

        @Schema(description = "결제 금액", example = "15000")
        @Min(value = 1, message = "결제 금액은 1원 이상이어야 합니다.")
        private int amount;
    }

    /**
     * 결제 실패 요청 (Fail Callback용)
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fail {
        @Schema(description = "에러 코드", example = "PAY_PROCESS_CANCELED")
        private String code;

        @Schema(description = "에러 메시지", example = "사용자가 결제를 취소하였습니다.")
        private String message;
    }

    /**
     * 결제 취소 요청
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "결제 취소 요청")
    public static class Cancel {
        @Schema(description = "취소 사유", example = "고객 단순 변심")
        @NotBlank(message = "취소 사유는 필수 입력값입니다.")
        private String cancelReason;
    }
}