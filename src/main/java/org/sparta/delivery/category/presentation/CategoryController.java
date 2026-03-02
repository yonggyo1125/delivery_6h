package org.sparta.delivery.category.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sparta.delivery.category.application.CategoryService;
import org.sparta.delivery.category.presentation.dto.CategoryRequestDto;
import org.sparta.delivery.category.presentation.dto.CategoryResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "카테고리 API", description = "카테고리 관리 및 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 전체 조회", description = "삭제되지 않은 모든 카테고리 목록을 조회합니다.")
    @GetMapping
    public List<CategoryResponseDto.Detail> getAllCategories() {
        return categoryService.getCategories().stream()
                .map(category -> CategoryResponseDto.Detail.builder()
                        .id(category.getId().getId())
                        .name(category.getName())
                        .build())
                .toList();
    }

    @Operation(summary = "카테고리 생성", description = "여러 개의 카테고리를 한 번에 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto createCategories(
            @RequestBody @Valid List<CategoryRequestDto.CategoryItem> request) {

        List<UUID> createdIds = categoryService.create(
                request.stream().map(CategoryRequestDto.CategoryItem::toServiceDto).toList()
        );

        return CategoryResponseDto.of(createdIds);
    }

    @Operation(summary = "카테고리 수정", description = "여러 개의 카테고리 이름을 한 번에 수정합니다.")
    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public void changeCategoryNames(
            @RequestBody @Valid List<CategoryRequestDto.CategoryItem> request) {

        categoryService.change(
                request.stream().map(CategoryRequestDto.CategoryItem::toServiceDto).toList()
        );
    }

    @Operation(summary = "카테고리 삭제", description = "여러 개의 카테고리를 한 번에 삭제합니다.")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategories(
            @RequestBody List<UUID> categoryIds) {

        categoryService.remove(categoryIds);
    }
}