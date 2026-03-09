package org.sparta.delivery.store.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sparta.delivery.store.application.dto.StoreServiceDto;
import org.sparta.delivery.store.application.product.ChangeProductService;
import org.sparta.delivery.store.application.product.CreateProductService;
import org.sparta.delivery.store.application.product.RemoveProductService;
import org.sparta.delivery.store.application.query.ProductQueryService;
import org.sparta.delivery.store.domain.query.dto.ProductQueryDto;
import org.sparta.delivery.store.presentation.dto.ProductQueryRequestDto;
import org.sparta.delivery.store.presentation.dto.ProductRequestDto;
import org.sparta.delivery.store.presentation.dto.ProductResponseDto;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "상품 API", description = "매장 내 메뉴(상품) 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/stores/{storeId}/products")
public class ProductController {

    private final ProductQueryService productQueryService;
    private final CreateProductService createProductService;
    private final ChangeProductService changeProductService;
    private final RemoveProductService removeProductService;

    @Operation(summary = "상품 단건 조회", description = "매장 내 특정 상품 코드로 상세 정보를 조회합니다.")
    @GetMapping("/{productCode}")
    public ProductResponseDto getProduct(
            @Parameter(description = "매장 ID") @PathVariable UUID storeId,
            @Parameter(description = "상품 코드") @PathVariable String productCode) {
        return productQueryService.getProduct(storeId, productCode);
    }

    @Operation(summary = "상품 목록 조회", description = "검색 조건에 맞는 매장 내 메뉴 목록을 조회합니다.")
    @GetMapping
    public List<ProductResponseDto> getProducts(
            @Parameter(description = "매장 ID") @PathVariable UUID storeId,
            @ParameterObject ProductQueryRequestDto request) {

        ProductQueryDto.Search searchCondition = ProductQueryDto.Search.builder()
                .name(request.name())
                .productCodes(request.productCodes())
                .categoryIds(request.categoryIds())
                .keyword(request.keyword())
                .build();

        return productQueryService.getProducts(storeId, searchCondition);
    }

    @Operation(summary = "상품 등록", description = "매장에 새로운 메뉴를 등록합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(
            @PathVariable UUID storeId,
            @RequestBody @Valid ProductRequestDto.Save request) {

        createProductService.create(storeId, toServiceDto(request));
    }

    @Operation(summary = "상품 정보 전체 수정", description = "상품의 이름, 가격, 옵션 등을 전체적으로 수정합니다.")
    @PutMapping("/{productCode}")
    public void changeProduct(
            @PathVariable UUID storeId,
            @PathVariable String productCode,
            @RequestBody @Valid ProductRequestDto.Save request) {

        changeProductService.changeProductInfo(storeId, productCode, toServiceDto(request));
    }

    @Operation(summary = "상품 상태 변경", description = "SALE(판매중), READY(준비중), STOCK_OUT(품절) 상태로 변경합니다.")
    @PatchMapping("/{productCode}/status")
    public void changeProductStatus(
            @PathVariable UUID storeId,
            @PathVariable String productCode,
            @Parameter(description = "변경할 상태 값", example = "SALE") @RequestParam String status) {

        changeProductService.changeProductStatus(storeId, productCode, status);
    }

    @Operation(summary = "단일 상품 삭제", description = "상품을 논리적으로 삭제(Soft Delete)합니다.")
    @DeleteMapping("/{productCode}")
    public void removeProduct(
            @PathVariable UUID storeId,
            @PathVariable String productCode) {

        removeProductService.remove(storeId, productCode);
    }

    @Operation(summary = "다중 상품 삭제", description = "여러 상품 코드를 받아 한꺼번에 삭제합니다.")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProducts(
            @PathVariable UUID storeId,
            @RequestBody List<String> productCodes) {

        removeProductService.remove(storeId, productCodes);
    }

    //  RequestDto -> StoreServiceDto
    private StoreServiceDto.Product toServiceDto(ProductRequestDto.Save request) {
        return StoreServiceDto.Product.builder()
                .productCode(request.getProductCode())
                .categoryId(request.getCategoryId())
                .name(request.getName())
                .aiGenerated(request.isAiGenerated())
                .aiContext(request.getAiContext())
                .price(request.getPrice())
                .options(request.getOptions() == null ? null :
                        request.getOptions().stream().map(this::toOptionServiceDto).toList())
                .build();
    }

    private StoreServiceDto.ProductOption toOptionServiceDto(ProductRequestDto.Option opt) {
        return StoreServiceDto.ProductOption.builder()
                .name(opt.getName())
                .price(opt.getPrice())
                .subOptions(opt.getSubOptions() == null ? null :
                        opt.getSubOptions().stream()
                                .map(s -> new StoreServiceDto.ProductSubOption(s.getName(), s.getAddPrice()))
                                .toList())
                .build();
    }
}