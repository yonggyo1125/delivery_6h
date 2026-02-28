package org.sparta.delivery.store.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.sparta.delivery.global.domain.BaseUserEntity;
import org.sparta.delivery.global.domain.service.AddressToCoords;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.global.presentation.exception.UnAuthorizedException;
import org.sparta.delivery.store.domain.dto.StoreDto;
import org.sparta.delivery.store.domain.exception.ProductNotFoundException;
import org.sparta.delivery.store.domain.service.OwnerCheck;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.IntStream;

/**
 * 모든 기능은 매장 주인(OWNER)와 관리자(MANAGER, MASTER)만 가능
 * 상품 수정 및 삭제시 매장 주인인 경우는 자신의 매장만, 관리자는 모두 가능
 * 분류 등록, 수정 시 유효한 분류인지 체크
 * 매장을 등록하면 기본 상태는 오픈 중비중 상태
 * 오픈 준비중이거나 운영시간이 아닌 경우는 주문 불가
 * 휴업중, 폐업중인 경우 노출 불가
 *
 */
@Entity
@ToString @Getter
@Table(name="P_STORE")
@Access(AccessType.FIELD)
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseUserEntity {
    @EmbeddedId
    private StoreId id;

    @Version
    private int version; // 낙관적 Lock

    @Column(length=30)
    @Enumerated(EnumType.STRING)
    private StoreStatus status; // 매장 운영 상태

    @Embedded
    private Owner owner;

    @Column(length=65, name="store_name")
    private String name; // 매장명

    @Embedded
    private StoreContact contact; // 매장 연락처

    @Embedded
    private StoreLocation location; // 매장 위치

    // 운영 요일 및 시간 - 1:N 관계 - 운영 요일 및 시간이 등록되지 않는다면 연중 무휴
    @ElementCollection(fetch=FetchType.LAZY)
    @CollectionTable(name="P_STORE_OPERATION", joinColumns=@JoinColumn(name="store_id"))
    @OrderColumn(name="operation_idx")
    private List<StoreOperation> operations;

    // 매장 분류 - 1:N 관계
    @ElementCollection(fetch=FetchType.LAZY)
    @CollectionTable(name="P_STORE_CATEGORY", joinColumns=@JoinColumn(name="store_id"))
    @SQLRestriction("deleted_at IS NULL")
    @OrderColumn(name="category_idx")
    private List<StoreCategory> categories;

    // 매장 메뉴 - 1:N 관계
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "P_PRODUCT", joinColumns = @JoinColumn(name="store_id"))
    @SQLRestriction("deleted_at IS NULL")
    @OrderColumn(name="product_idx")
    private List<Product> products;

    @Builder
    public Store(UUID storeId, UUID ownerId, String ownerName, String landline, String email, String address, List<UUID> categoryIds, AddressToCoords addressToCoords, RoleCheck roleCheck, OwnerCheck ownerCheck) {

        // 등록 권한 체크
        checkAuthority(roleCheck, ownerCheck);

        this.id = storeId == null ? StoreId.of() : StoreId.of(storeId);
        this.owner = new Owner(ownerId, ownerName);
        this.contact = new StoreContact(landline, email);
        this.location = new StoreLocation(address, addressToCoords);
        this.status = StoreStatus.PREPARING;

        // 분류 추가
        createCategory(StoreDto.CategoryDto
                .builder()
                .roleCheck(roleCheck)
                .ownerCheck(ownerCheck)
                .categoryIds(categoryIds)
                .build());
    }


    // 상점 삭제(Soft Delete)
    public void remove() {
        deletedAt = LocalDateTime.now();
    }

    //// 운영 요일 및 시간 S
    // 생성
    public void createOperation(StoreDto.OperationDto dto) {
        checkAuthority(dto.getRoleCheck(), dto.getOwnerCheck());
        operations = Objects.requireNonNullElseGet(operations, ArrayList::new);

        operations.add(StoreDto.toOperation(dto));
    }

    // 여러개 생성
    public void createOperation(List<StoreDto.OperationDto> items) {
        items.forEach(this::createOperation);
    }

    // 변경
    public void changeOperation(int idx, StoreDto.OperationDto dto) {
        checkAuthority(dto.getRoleCheck(), dto.getOwnerCheck());
         if (operations == null || operations.get(idx) == null) return;
        operations.set(idx, StoreDto.toOperation(dto));
    }

    // 제거
    public void removeOperation(RoleCheck roleCheck, OwnerCheck ownerCheck, List<Integer> idxes) {
         // 권한 체크
        checkAuthority(roleCheck, ownerCheck);
        if (operations == null) return;

        List<StoreOperation> remaining = IntStream.range(0, operations.size())
                .filter(i -> !idxes.contains(i))
                .mapToObj(operations::get)
                .toList();

        operations.clear();
        operations.addAll(remaining);
    }

    //// 운영 요일 및 시간  E

    ////  상품 S
    // 상품 생성
    public void createProduct(StoreDto.ProductDto dto) {
        // 권한 체크
        checkAuthority(dto.getRoleCheck(), dto.getOwnerCheck());

        products = Objects.requireNonNullElseGet(products, ArrayList::new);

        products.add(StoreDto.toProduct(dto));
    }

    // 상품 수정
    public void changeProduct(int productIdx, StoreDto.ProductDto dto) {
        // 권한 체크
        checkAuthority(dto.getRoleCheck(), dto.getOwnerCheck());
        if (products == null || products.get(productIdx) == null) {
            throw new ProductNotFoundException();
        }

        products.set(productIdx, StoreDto.toProduct(dto));
    }

    // 상품 삭제 (Soft Delete)
    public void removeProduct(RoleCheck roleCheck, OwnerCheck ownerCheck, List<String> productCodes) {
        checkAuthority(roleCheck, ownerCheck);

        if (products == null || productCodes == null || productCodes.isEmpty()) {
            return;
        }

        Set<String> codeSet = new HashSet<>(productCodes);

        products.stream()
                .filter(p -> p.getDeletedAt() == null && codeSet.contains(p.getProductCode()))
                .forEach(Product::remove);

    }
    ////  상품 E

    ///// 카테고리 S
    // 카테고리 생성
    public void createCategory(StoreDto.CategoryDto dto) {
        // 권한 체크
        checkAuthority(dto.getRoleCheck(), dto.getOwnerCheck());

        List<UUID> categoryIds = dto.getCategoryIds();
        if (categoryIds == null || categoryIds.isEmpty()) return;

        categories = Objects.requireNonNullElseGet(categories, ArrayList::new);
        categories.addAll(categoryIds.stream().distinct().map(StoreCategory::new).toList());
    }


    // 카테고리 모두 지우기
    public void truncateCategory(RoleCheck roleCheck, OwnerCheck ownerCheck) {
        // 권한 체크
        checkAuthority(roleCheck, ownerCheck);
        if (categories != null) categories.clear();
    }

    // 카테고리 교체
    public void replaceCategory(StoreDto.CategoryDto dto) {
        truncateCategory(dto.getRoleCheck(),dto.getOwnerCheck());
        createCategory(dto);
    }

    // 카테고리 제거(Soft Delete)
    public void removeCategory(StoreDto.CategoryDto dto) {
        // 권한 체크
        checkAuthority(dto.getRoleCheck(), dto.getOwnerCheck());

        if (categories == null || dto.getCategoryIds() == null) return;

        Set<UUID> targetIds = new HashSet<>(dto.getCategoryIds());

        categories.stream()
                .filter(c -> c.getDeletedAt() == null && targetIds.contains(c.getCategoryId()))
                .forEach(StoreCategory::remove);
    }
    ///// 카테고리 E

    /**
     *  영업중이고 영업일 및 시간에 해당하는 경우 주문 가능
     *  startTime 보다 endTime이 더 앞선 시간인 경우 endTime은 익일 시간으로 판단
     *      예) 16:00, 02:00 이면 02:00은 익일 새벽 2시
     *  영업일 및 시간이 등록되지 않은 경우는 breakTime 제외 항상 주문가능
     *  시간은 등록되지 않고 요일만 등록된 경우 시간과 상관없이 운영
     */
    public boolean isOrderable() {
        if (status != StoreStatus.OPEN) return false;
        if (operations == null || operations.isEmpty()) return true;

        LocalDateTime now = LocalDateTime.now();
        DayOfWeek today = now.getDayOfWeek();
        DayOfWeek yesterday = today.minus(1);

        // 1. 어제부터 시작된 영업이 현재(새벽)까지 이어지는지 확인
        boolean isContinuingFromYesterday = operations.stream()
                .filter(op -> op.getDayOfWeek().equals(yesterday))
                .anyMatch(op -> isNowInBusinessRange(op, now, true));

        // 2. 오늘 시작된 영업이 현재 진행 중인지 확인
        boolean isStartingToday = operations.stream()
                .filter(op -> op.getDayOfWeek().equals(today))
                .anyMatch(op -> isNowInBusinessRange(op, now, false));

        return isContinuingFromYesterday || isStartingToday;
    }

    private boolean isNowInBusinessRange(StoreOperation op, LocalDateTime now, boolean isFromYesterday) {
        LocalTime start = op.getStartHour();
        LocalTime end = op.getEndHour();

        // 시간 설정이 없으면 요일만 맞으면 통과 (어제 영업이면 false)
        if (start == null || end == null) return !isFromYesterday;

        LocalDate baseDate = isFromYesterday ? now.toLocalDate().minusDays(1) : now.toLocalDate();
        LocalDateTime businessStart = baseDate.atTime(start);
        LocalDateTime businessEnd = baseDate.atTime(end);

        // 익일 종료 케이스 처리 (ex: 22:00 ~ 02:00)
        if (businessEnd.isBefore(businessStart)) {
            businessEnd = businessEnd.plusDays(1);
        }

        // 현재 시간이 영업 범위 밖이면 즉시 종료
        if (now.isBefore(businessStart) || now.isAfter(businessEnd)) {
            return false;
        }

        // 영업 범위 안이라면, 해당 'Operation' 객체에 설정된 브레이크 타임인지 확인
        return !isWithinBreakTime(op, now, baseDate);
    }

    private boolean isWithinBreakTime(StoreOperation op, LocalDateTime now, LocalDate baseDate) {
        return isTimeInBreak(op.getBreakHour1(), now, baseDate) ||
                isTimeInBreak(op.getBreakHour2(), now, baseDate);
    }

    private boolean isTimeInBreak(BreakTime breakTime, LocalDateTime now, LocalDate baseDate) {
        if (breakTime == null || breakTime.start() == null || breakTime.end() == null) return false;

        LocalDateTime sTime = baseDate.atTime(breakTime.start());
        LocalDateTime eTime = baseDate.atTime(breakTime.end());

        // 브레이크 타임도 자정을 넘길 수 있음을 고려
        if (eTime.isBefore(sTime)) eTime = eTime.plusDays(1);

        // inclusive 체크 (start <= now < end)
        return (now.isEqual(sTime) || now.isAfter(sTime)) && now.isBefore(eTime);
    }

    // 영업 준비중, 영업중인 경우 가게 노출 가능
    public boolean isVisible() {
        return status == StoreStatus.OPEN || status == StoreStatus.PREPARING;
    }

    /**
     * 모든 기능은 매장 주인(OWNER)와 관리자(MANAGER, MASTER)만 가능
     * storeId가 null 이라면 신규 등록이므로 ONWER 권한이 있는지만 체크,
     *          null이 아니라면 storeId로 매장의 소유자인지 체크
     */
    private void checkAuthority(RoleCheck roleCheck, OwnerCheck ownerCheck) {

        // 관리자 권한인 경우 통과
        if (roleCheck.hasRole(List.of("MANAGER", "MASTER"))) {
            return;
        }

        // 신규 등록인 경우라면 OWNER 권한 확인
        if (id == null) {
            if (!roleCheck.hasRole("OWNER")) {
                throw new UnAuthorizedException();
            }
        } else if (!ownerCheck.isOwner(id.getId())) { // 상점 정보 수정인 경우 매장 소유주 확인
            throw new UnAuthorizedException();
        }
    }
}
