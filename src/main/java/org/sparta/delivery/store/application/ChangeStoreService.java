package org.sparta.delivery.store.application;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.global.domain.service.AddressToCoords;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.store.application.dto.StoreServiceDto;
import org.sparta.delivery.store.domain.Store;
import org.sparta.delivery.store.domain.StoreId;
import org.sparta.delivery.store.domain.StoreRepository;
import org.sparta.delivery.store.domain.StoreStatus;
import org.sparta.delivery.store.domain.dto.StoreDto;
import org.sparta.delivery.store.domain.exception.StoreNotFoundException;
import org.sparta.delivery.store.domain.service.OwnerCheck;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChangeStoreService {
    private final StoreRepository repository;
    private final RoleCheck roleCheck;
    private final OwnerCheck ownerCheck;
    private final AddressToCoords addressToCoords;

    //// 카테고리 S
    // 카테고리 추가
    @Transactional
    public void addCategory(UUID storeId, List<UUID> categoryIds) {
        Store store = getStore(storeId);
        store.createCategory(toCategoryDto(categoryIds));
    }

    @Transactional
    public void removeCategory(UUID storeId, List<UUID> categoryIds) {
        Store store = getStore(storeId);
        store.removeCategory(toCategoryDto(categoryIds));
    }

    @Transactional
    public void replaceCategory(UUID storeId, List<UUID> categoryIds) {
        Store store = getStore(storeId);
        store.replaceCategory(toCategoryDto(categoryIds));
    }

    @Transactional
    public void truncateCategory(UUID storeId) {
        Store store = getStore(storeId);
        store.truncateCategory(roleCheck, ownerCheck);
    }
    //// 카테고리 E

    //// 운영 요일 및 시간 S
    // 운영 요일 및 시간 등록
    @Transactional
    public void createOperations(UUID storeId, List<StoreServiceDto.Operation> items) {
        Store store = getStore(storeId);

        List<StoreDto.OperationDto> operations = items.stream().map(this::toOperationDto).toList();
        store.createOperation(operations);
    }

    // 운영 요일 및 시간 변경
    @Transactional
    public void changeOperation(UUID storeId, int operationIdx, StoreServiceDto.Operation item) {
        Store store = getStore(storeId);

        store.changeOperation(operationIdx, toOperationDto(item));
    }

    // 운영 요일 및 시간 제거
    @Transactional
    public void removeOperations(UUID storeId, List<Integer> operationIdxes) {
        Store store = getStore(storeId);

        store.removeOperation(roleCheck, ownerCheck, operationIdxes);
    }

    //// 운영 요일 및 시간 E

    // 매장 일반 정보 수정
    @Transactional
    public void changeStoreInfo(UUID storeId, StoreServiceDto.StoreInfo dto) {
        Store store = getStore(storeId);
        store.changeInfo(StoreDto.StoreInfoDto.builder()
                        .roleCheck(roleCheck)
                        .ownerCheck(ownerCheck)
                        .ownerName(dto.getOwnerName())
                        .businessNo(dto.getBusinessNo())
                        .landline(dto.getLandline())
                        .email(dto.getEmail())
                        .address(dto.getAddress())
                        .addressToCoords(addressToCoords)
                .build());
    }

    // 매장 운영 상태 변경
    @Transactional
    public void changeStoreStatus(UUID storeId, String status) {
        Store store = getStore(storeId);

        StoreStatus targetStatus = StoreStatus.valueOf(status.toUpperCase());
        store.changeStatus(roleCheck, ownerCheck, targetStatus);
    }

    // 매장 조회
    private Store getStore(UUID storeId) {
        return repository.findById(StoreId.of(storeId)).orElseThrow(StoreNotFoundException::new);
    }

    // List<UUID> categoryIds -> StoreDto.CategoryDto 변환
    private StoreDto.CategoryDto toCategoryDto(List<UUID> categoryIds) {
        return StoreDto.CategoryDto.builder()
                .roleCheck(roleCheck)
                .ownerCheck(ownerCheck)
                .categoryIds(categoryIds)
                .build();
    }

    // StoreServiceDto.Operation  -> StoreDto.OperationDto
    public StoreDto.OperationDto toOperationDto(StoreServiceDto.Operation operation) {
        return StoreDto.OperationDto.builder()
                .roleCheck(roleCheck)
                .ownerCheck(ownerCheck)
                .startHour(operation.getStartHour())
                .endHour(operation.getEndHour())
                .dayOfWeek(operation.getDayOfWeek())
                .breakStart1(operation.getBreakStart1())
                .breakEnd1(operation.getBreakEnd1())
                .breakStart2(operation.getBreakStart2())
                .breakEnd2(operation.getBreakEnd2())
                .build();
    }
}
