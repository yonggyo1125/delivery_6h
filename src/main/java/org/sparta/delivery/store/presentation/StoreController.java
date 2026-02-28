package org.sparta.delivery.store.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sparta.delivery.store.application.ChangeStoreService;
import org.sparta.delivery.store.application.CreateStoreService;
import org.sparta.delivery.store.application.RemoveStoreService;
import org.sparta.delivery.store.application.dto.StoreServiceDto;
import org.sparta.delivery.store.presentation.dto.StoreRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "매장 API", description = "매장 관리(등록, 수정, 상태 변경, 삭제)를 담당하는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/stores")
public class StoreController {

    private final CreateStoreService createStoreService;
    private final ChangeStoreService changeStoreService;
    private final RemoveStoreService removeStoreService;

    @Operation(summary = "매장 신규 등록", description = "새로운 매장을 시스템에 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping
    public ResponseEntity<UUID> createStore(
            @RequestBody @Valid StoreRequestDto.Create request) {
        StoreServiceDto.CreateStore serviceDto = StoreServiceDto.CreateStore.builder()
                .landline(request.getLandline())
                .email(request.getEmail())
                .address(request.getAddress())
                .categoryId(request.getCategoryIds())
                .build();

        UUID createdStoreId = createStoreService.create(serviceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStoreId);
    }

    @Operation(summary = "매장 정보 수정", description = "매장의 기본 정보를 업데이트합니다.")
    @PatchMapping("/{storeId}/info")
    public ResponseEntity<Void> updateStoreInfo(
            @Parameter(description = "매장 식별자(UUID)") @PathVariable UUID storeId,
            @RequestBody @Valid StoreRequestDto.ChangeInfo request) {
        changeStoreService.changeStoreInfo(storeId, StoreServiceDto.StoreInfo.builder()
                .ownerName(request.getOwnerName())
                .businessNo(request.getBusinessNo())
                .landline(request.getLandline())
                .email(request.getEmail())
                .address(request.getAddress())
                .build());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "매장 운영 상태 변경", description = "매장의 상태(OPEN, PREPARING 등)를 변경합니다.")
    @PatchMapping("/{storeId}/status")
    public ResponseEntity<Void> updateStoreStatus(
            @PathVariable UUID storeId,
            @Parameter(description = "변경할 상태 값", example = "OPEN") @RequestParam String status) {
        changeStoreService.changeStoreStatus(storeId, status);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "운영 시간 일괄 등록/변경", description = "요일별 운영 시간 및 브레이크 타임을 설정합니다.")
    @PostMapping("/{storeId}/operations")
    public ResponseEntity<Void> createOperations(
            @PathVariable UUID storeId,
            @RequestBody @Valid List<StoreRequestDto.Operation> requests) {
        List<StoreServiceDto.Operation> serviceDtos = requests.stream()
                .map(this::toOperationServiceDto)
                .toList();
        changeStoreService.createOperations(storeId, serviceDtos);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "매장 삭제(Soft Delete)", description = "매장을 논리적으로 삭제 처리합니다.")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable UUID storeId) {
        removeStoreService.remove(storeId);
        return ResponseEntity.noContent().build();
    }

    private StoreServiceDto.Operation toOperationServiceDto(StoreRequestDto.Operation request) {
        return StoreServiceDto.Operation.builder()
                .dayOfWeek(request.getDayOfWeek())
                .startHour(request.getStartHour())
                .endHour(request.getEndHour())
                .breakStart1(request.getBreakStart1())
                .breakEnd1(request.getBreakEnd1())
                .breakStart2(request.getBreakStart2())
                .breakEnd2(request.getBreakEnd2())
                .build();
    }
}