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
import org.sparta.delivery.store.application.query.StoreQueryService;
import org.sparta.delivery.store.presentation.dto.StoreQueryRequestDto;
import org.sparta.delivery.store.presentation.dto.StoreRequestDto;
import org.sparta.delivery.store.presentation.dto.StoreResponseDto;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "매장 API", description = "매장 관리 및 조회를 담당하는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/stores")
public class StoreController {

    private final StoreQueryService storeQueryService;

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
    public void updateStoreInfo(
            @Parameter(description = "매장 식별자(UUID)") @PathVariable UUID storeId,
            @RequestBody @Valid StoreRequestDto.ChangeInfo request) {
        changeStoreService.changeStoreInfo(storeId, StoreServiceDto.StoreInfo.builder()
                .ownerName(request.getOwnerName())
                .businessNo(request.getBusinessNo())
                .landline(request.getLandline())
                .email(request.getEmail())
                .address(request.getAddress())
                .build());
    }

    @Operation(summary = "매장 운영 상태 변경", description = "매장의 상태(OPEN, PREPARING 등)를 변경합니다.")
    @PatchMapping("/{storeId}/status")
    public void updateStoreStatus(
            @PathVariable UUID storeId,
            @Parameter(description = "변경할 상태 값", example = "OPEN") @RequestParam String status) {
        changeStoreService.changeStoreStatus(storeId, status);
    }

    @Operation(summary = "운영 시간 일괄 등록/변경", description = "요일별 운영 시간 및 브레이크 타임을 설정합니다.")
    @PostMapping("/{storeId}/operations")
    public void createOperations(
            @PathVariable UUID storeId,
            @RequestBody @Valid List<StoreRequestDto.Operation> requests) {
        List<StoreServiceDto.Operation> serviceDtos = requests.stream()
                .map(this::toOperationServiceDto)
                .toList();
        changeStoreService.createOperations(storeId, serviceDtos);
    }

    @Operation(summary = "매장 삭제(Soft Delete)", description = "매장을 논리적으로 삭제 처리합니다.")
    @DeleteMapping("/{storeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStore(@PathVariable UUID storeId) {
        removeStoreService.remove(storeId);
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

    @Operation(summary = "매장 단건 상세 조회", description = "ID를 통해 특정 매장의 모든 공개 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "매장을 찾을 수 없음")
    @GetMapping("/{storeId}")
    public StoreResponseDto getStore(@PathVariable UUID storeId) {
        return storeQueryService.getStore(storeId);
    }

    @Operation(summary = "매장 다중 조건 검색", description = "카테고리, 이름, 상태, 지역별로 매장을 필터링하여 검색합니다.")
    @GetMapping
    public Page<StoreResponseDto> searchStores(
            @ParameterObject StoreQueryRequestDto.Search request,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {


        return storeQueryService.searchStores(request.toSearchCondition(), pageable);
    }

    @Operation(summary = "주변 매장 GPS 조회", description = "현재 위/경도 좌표를 기준으로 반경 내 매장을 가까운 순으로 조회합니다.")
    @GetMapping("/nearest")
    public Page<StoreResponseDto> getNearestStores(
            @ParameterObject StoreQueryRequestDto.Nearest request,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {

        return storeQueryService.getNearestStores(
                request.getLatitude(),
                request.getLongitude(),
                request.getRadiusKm(),
                pageable);
    }
}