package org.sparta.delivery.order.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sparta.delivery.global.domain.BaseUserEntity;
import org.sparta.delivery.global.domain.Price;
import org.sparta.delivery.global.domain.exception.BadRequestException;
import org.sparta.delivery.global.domain.exception.UnAuthorizedException;
import org.sparta.delivery.global.domain.service.OwnerCheck;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.global.domain.service.UserDetails;
import org.sparta.delivery.global.infrastructure.event.Events;
import org.sparta.delivery.order.domain.event.OrderAcceptEvent;
import org.sparta.delivery.order.domain.event.OrderRefundEvent;
import org.sparta.delivery.order.domain.exception.InvalidOrderItemException;
import org.sparta.delivery.order.domain.exception.OrderItemNotExistException;
import org.sparta.delivery.order.domain.service.OrderCheck;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.sparta.delivery.order.domain.OrderStatus.*;

/**
 * 0. 주문은 반드시 회원의 권한을 가진 사용자만 가능
 * 1. 주문 상품이 1개 이상이어야 주문이 가능
 * 2. 주문 상품은 주문이 가능한 상품인지 체크 한다.
 *    - 1. 매장의 영업 여부 체크: Store::isVisible()
 *    - 2. 매장의 주문 가능 시간 체크: Store::isOrderable()
 *    - 3. 상품의 주문 가능 여부 체크: Product::isOrderable()
 *    - 주문상품에는 여러 매장이 있을 수 있으므로 목록에서 체크
 *
 * 3. 주문 상품의 총 금액은 주문상품 목록을 통해서만 계산된다.
 * 4. 주문 취소는 주문 접수 후 5분 이내 가능
 *      - 입금 확인 전 : 주문 취소 상태(ORDER_CANCEL)
 *      - 입금 완료 후 : 주문 환불 상태(ORDER_REFUND) / 결제 취소 진행(이벤트 발생)
 * 5. 배송중 주문 상태는 입금 확인이 되어야만 변경 가능
 * 6. 배송정보 변경은 배송중 이전 단계에서만 가능
 */

@Entity
@ToString @Getter
@Table(name="P_ORDER")
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseUserEntity {
    @EmbeddedId
    private OrderId id;

    @Embedded
    private Orderer orderer;

    @Embedded
    private StoreInfo storeInfo;

    @Embedded
    private DeliveryInfo deliveryInfo;

    private double reviewScore; // 리뷰 점수

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="P_ORDER_ITEM", joinColumns = @JoinColumn(name="order_id"))
    @OrderColumn(name="item_idx")
    private List<OrderItem> orderItems;

    @AttributeOverrides(
        @AttributeOverride(name="value", column = @Column(name="total_order_price"))
    )
    private Price totalOrderPrice;

    @Enumerated(EnumType.STRING)
    @Column(length=45)
    private OrderStatus status;

    @Builder
    public Order(UUID orderId, String ordererName, String ordererEmail, UUID storeId, String storeName, String storeAddress, String storeTel, List<OrderItem> orderItems, String deliveryAddress, String deliveryAddressDetail, String deliveryMemo, OrderCheck orderCheck, UserDetails userDetails) {

        // 로그인 여부 체크
        checkAuthenticated(userDetails);

        this.id = orderId == null ? OrderId.of() : OrderId.of(orderId);
        this.orderer = new Orderer(
                    userDetails.getId(),
                    StringUtils.hasText(ordererName) ? ordererName : userDetails.getName(),
                    StringUtils.hasText(ordererEmail) ? ordererEmail : userDetails.getEmail()
        );
        this.storeInfo = new StoreInfo(storeId, storeName, storeAddress, storeTel); // 매장 정보
        this.deliveryInfo = new DeliveryInfo(deliveryAddress, deliveryAddressDetail, deliveryMemo); // 배송 정보
        this.status = ORDER_CREATING; // 주문 생성 중
        setOrderItems(orderItems, orderCheck);
        calculateTotalOrderPrice();
    }

    private void setOrderItems(List<OrderItem> orderItems, OrderCheck orderCheck) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new OrderItemNotExistException();
        }

        if (!orderCheck.isOrderable(storeInfo.getStoreId(), orderItems)) { // 주문 가능 상품 여부 체크
            throw new InvalidOrderItemException();
        }

        this.orderItems = orderItems;
    }

    private void calculateTotalOrderPrice() {
        this.totalOrderPrice = new Price(orderItems.stream().mapToInt(x -> x.getTotalPrice().getValue()).sum());
    }

    // 주문 접수
    public void orderAccept(RoleCheck roleCheck, OwnerCheck ownerCheck, OrderCheck orderCheck) {
        // 권한 체크
        checkAuthority(roleCheck, ownerCheck, orderCheck);

        this.status = ORDER_ACCEPT;

        // 주문 접수 후 이벤트 발생 시키기 - 메일 전송
        Events.trigger(new OrderAcceptEvent(id.getId()));
    }

    // 주문 취소
    public void cancel(RoleCheck roleCheck, OwnerCheck ownerCheck, OrderCheck orderCheck) {
        // 권한 체크
        checkAuthority(roleCheck, ownerCheck, orderCheck);

        //  주문 접수 상태(입금 전) + 5분 이내 취소 시 -> 단순 주문 취소
        if (status == ORDER_ACCEPT) {
            if (createdAt == null || LocalDateTime.now().isBefore(createdAt.plusMinutes(5L))) {
                this.status = OrderStatus.ORDER_CANCEL;
            } else {
                throw new BadRequestException("주문 접수 후 5분이 지나 취소가 불가능합니다.");
            }
        }
        // 입금 확인 상태(PAYMENT_CONFIRM)에서 취소 시 -> 환불 처리 및 이벤트 발생
        else if (status == PAYMENT_CONFIRM) {
            this.status = OrderStatus.ORDER_REFUND;

            // 결제 취소 요청 이벤트 트리거
            Events.trigger(new OrderRefundEvent(id.getId()));
        }
    }

    /**
     * 배송 시작
     * 배송 시작은 입금 확인 후 진행, 그러나 배송지가 매장에서 배송 가능한 지역이 아니라면 배송 불가 함
     * 매장별 배송 불가 지역 체크 필요, 그러나 이 기능은 다른 도메인 기능이 필요하므로 도메인 서비스로 추가, 단순히 도메인 서비스에 주문 도메인의 delivery상태 변경 로직은 실행
     */
    public void delivery(RoleCheck roleCheck, OwnerCheck ownerCheck, OrderCheck orderCheck) {
        if (this.status != PAYMENT_CONFIRM) {
            return;
        }

        // 권한 체크
        checkAuthority(roleCheck, ownerCheck, orderCheck);

        this.status = OrderStatus.DELIVERY;
    }

    /**
     * 주문서의 주소 변경
     *
     * 1. 주문자, 매장 점주, 관리자(MASTER, MANAGER) 변경 가능
     * 2. 배송중 이전 단계에서만 가능
     */
    public void changeDeliveryInfo(RoleCheck roleCheck, OwnerCheck ownerCheck, OrderCheck orderCheck) {
        if (!List.of(ORDER_CREATING, ORDER_ACCEPT, PAYMENT_CONFIRM, PREPARING).contains(status)){
            return;
        }

        checkAuthority(roleCheck, ownerCheck, orderCheck);

    }

    /**
     * 주문서 정보 변경 가능 여부 체크
     *
     * 1. 자신의 주문은 수정 가능
     * 2. 주문서에 등록된 매장 ID의 점주인 경우 가능
     * 3. 관리자(MASTER, MANAGER)인 경우 가능
     */
    private void checkAuthority(RoleCheck roleCheck, OwnerCheck ownerCheck, OrderCheck orderCheck) {

        // 관리자 & 매장 점주 & 자신의 주문인 경우 true
        if (roleCheck.hasRole(List.of("MASTER", "MANAGER")) || ownerCheck.isOwner(storeInfo.getStoreId()) || orderCheck.isMyOrder(id)) {
            return;
        }

        throw new UnAuthorizedException();
    }

    // 로그인 여부 체크
    private void checkAuthenticated(UserDetails userDetails) {
        if (userDetails.getId() == null || !userDetails.isAuthenticated()) {
            throw new UnAuthorizedException("로그인이 필요한 서비스입니다.");
        }
    }
}
