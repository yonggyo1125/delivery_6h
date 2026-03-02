package org.sparta.delivery.order.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sparta.delivery.global.domain.service.UserDetails;
import org.sparta.delivery.order.application.query.OrderQueryService;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "주문 API", description = "주문 조회 관련 API")
@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderQueryService orderQueryService;
    private final UserDetails userDetails;

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