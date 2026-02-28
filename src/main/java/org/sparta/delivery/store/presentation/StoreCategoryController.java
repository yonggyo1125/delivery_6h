package org.sparta.delivery.store.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sparta.delivery.store.application.ChangeStoreService;
import org.sparta.delivery.store.presentation.dto.CategoryRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "2. 매장 및 상품 관리", description = "매장 내 카테고리 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/stores/{storeId}/categories")
public class StoreCategoryController {

    private final ChangeStoreService changeStoreService;

    @Operation(summary = "카테고리 추가", description = "기존 매장 카테고리 목록에 새로운 카테고리 ID들을 추가합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addCategory(
            @PathVariable UUID storeId,
            @RequestBody @Valid CategoryRequestDto.Update request) {
        changeStoreService.addCategory(storeId, request.getCategoryIds());
    }

    @Operation(summary = "카테고리 전체 교체", description = "매장의 카테고리 목록을 전달받은 리스트로 완전히 대체합니다.")
    @PutMapping
    public void replaceCategory(
            @PathVariable UUID storeId,
            @RequestBody @Valid CategoryRequestDto.Update request) {
        changeStoreService.replaceCategory(storeId, request.getCategoryIds());
    }

    @Operation(summary = "카테고리 선택 제거", description = "매장 카테고리 목록에서 특정 ID들을 찾아 제거합니다.")
    @DeleteMapping
    public void removeCategory(
            @PathVariable UUID storeId,
            @RequestBody @Valid CategoryRequestDto.Update request) {
        changeStoreService.removeCategory(storeId, request.getCategoryIds());
    }

    @Operation(summary = "카테고리 전체 비우기", description = "해당 매장의 모든 카테고리 설정을 초기화합니다.")
    @DeleteMapping("/all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void truncateCategory(@PathVariable UUID storeId) {
        changeStoreService.truncateCategory(storeId);
    }
}