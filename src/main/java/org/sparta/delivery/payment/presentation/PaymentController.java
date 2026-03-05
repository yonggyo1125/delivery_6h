package org.sparta.delivery.payment.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.delivery.payment.application.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
@Tag(name = "Payment API", description = "결제 승인 및 취소를 담당하는 API입니다.")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "결제 승인 처리", description = "토스 결제창 성공 시 리다이렉트되어 승인을 확정하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 승인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 결제 요청 또는 금액 불일치"),
            @ApiResponse(responseCode = "404", description = "결제 정보를 찾을 수 없음")
    })
    @GetMapping("/success")
    public void success(@Valid PaymentRequestDto.Approve request) {

        paymentService.approve(request.getOrderId(), request.getPaymentKey());
    }

    @Operation(summary = "결제 실패 처리", description = "토스 결제창 실패 시 에러 정보를 받는 콜백 API입니다.")
    @GetMapping("/fail")
    public void fail(PaymentRequestDto.Fail request) {

        // 실패 콜백 구현 예정
    }

    @Operation(summary = "결제 취소", description = "완료된 결제를 취소(환불) 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "결제 취소 성공"),
            @ApiResponse(responseCode = "400", description = "취소 불가능한 상태"),
            @ApiResponse(responseCode = "404", description = "결제 정보를 찾을 수 없음")
    })
    @PostMapping("/{paymentId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(
            @PathVariable UUID paymentId,
            @RequestBody @Valid  PaymentRequestDto.Cancel request) {

        paymentService.cancel(paymentId, request.getCancelReason());
    }
}