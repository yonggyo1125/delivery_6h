package org.sparta.delivery.order.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sparta.delivery.global.domain.service.UserDetails;
import org.sparta.delivery.order.application.ChangeOrderService;
import org.sparta.delivery.order.application.CreateOrderService;
import org.sparta.delivery.order.application.query.OrderQueryService;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "주문 API", description = "주문 조회 관련 API")
@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderService createOrderService;
    private final ChangeOrderService changeOrderService;
    private final OrderQueryService orderQueryService;
    private final UserDetails userDetails;

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다. 초기 상태는 ORDER_CREATING입니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto.CreateResult createOrder(@RequestBody @Valid OrderRequestDto.Create request) {
        UUID orderId = createOrderService.createOrder(request.toServiceDto());
        return new OrderResponseDto.CreateResult(orderId);
    }

    @Operation(summary = "배송지 정보 변경", description = "배송 시작 전 단계에서 배송지 주소 및 메모를 변경합니다.")
    @PatchMapping("/{orderId}/delivery-info")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeDeliveryInfo(
            @PathVariable UUID orderId,
            @RequestBody @Valid OrderRequestDto.ChangeDelivery request) {
        changeOrderService.changeDeliveryInfo(request.toServiceDto(orderId));
    }

    @Operation(summary = "주문 취소/환불", description = "주문을 취소합니다. 입금 후라면 환불 프로세스가 진행됩니다.")
    @PatchMapping("/{orderId}/cancel")
    public void cancelOrder(@PathVariable UUID orderId) {
        changeOrderService.cancelOrder(orderId);
    }

    @Operation(summary = "배송 시작", description = "결제 확인이 된 주문에 대해 배송을 시작합니다.")
    @PatchMapping("/{orderId}/delivery")
    public void startDelivery(@PathVariable UUID orderId) {
        changeOrderService.startDelivery(orderId);
    }

    @Operation(summary = "주문 완료", description = "배송이 완료된 주문을 최종 완료 처리합니다. 리뷰 작성 이벤트가 발생합니다.")
    @PatchMapping("/{orderId}/done")
    public void doneOrder(@PathVariable UUID orderId) {
        changeOrderService.completeOrder(orderId);
    }

    @Operation(summary = "주문 상세 조회", description = "특정 주문의 상세 내역과 상품 정보를 조회합니다.")
    @GetMapping("/{orderId}")
    public OrderResponseDto.OrderDetail getOrderDetail(@PathVariable UUID orderId) {
        return orderQueryService.getOrderDetail(orderId);
    }

    /**
     * @PageableAsQueryParam: Swagger UI에 page, size, sort 파라미터를 생성함
     * @Parameter(hidden = true): 기본 Pageable 객체의 복잡한 구조가 노출되는 것을 방지함
     * @ParameterObject: Search DTO의 필드들을 평평하게(flat) 펼쳐서 쿼리 파라미터로 인식하게 함
     */
    @Operation(summary = "나의 주문 목록 조회", description = "로그인한 사용자의 주문 내역을 페이징하여 조회합니다.")
    @GetMapping("/my")
    @PageableAsQueryParam
    public Page<OrderResponseDto.Order> getMyOrders(
            @ParameterObject OrderRequestDto.Search request,
            @Parameter(hidden = true) Pageable pageable) {
        return orderQueryService.getUserOrders(
                userDetails.getId(), request.toQuerySearch(), pageable);
    }

    @Operation(summary = "매장 주문 목록 조회", description = "점주가 자신의 매장에 들어온 주문 내역을 조회합니다.")
    @GetMapping("/store/{storeId}")
    @PageableAsQueryParam
    public Page<OrderResponseDto.Order> getStoreOrders(
            @PathVariable UUID storeId,
            @ParameterObject OrderRequestDto.Search request,
            @Parameter(hidden = true) Pageable pageable) {
        return orderQueryService.getStoreOrders(
                storeId, request.toQuerySearch(), pageable);
    }

    @Operation(summary = "전체 주문 목록 조회", description = "시스템의 모든 주문 내역을 조회합니다 (관리자용).")
    @GetMapping
    @PageableAsQueryParam
    public Page<OrderResponseDto.Order> getAllOrders(
            @ParameterObject OrderRequestDto.Search request,
            @Parameter(hidden = true) Pageable pageable) {
        return orderQueryService.getOrders(
                request.toQuerySearch(), pageable);
    }
}