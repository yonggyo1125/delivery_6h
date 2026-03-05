package org.sparta.delivery.review.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sparta.delivery.review.application.ReviewService;
import org.sparta.delivery.review.application.query.ReviewQueryService;
import org.sparta.delivery.review.domain.query.ReviewQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Review API", description = "V1 리뷰 관리 API (등록/수정/삭제/조회)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewQueryService reviewQueryService;

    @Operation(summary = "리뷰 등록", description = "완료된 주문에 대해 리뷰를 작성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UUID create(@Valid @RequestBody ReviewRequestDto.Create request) {
        return reviewService.create(request.toServiceDto());
    }

    @Operation(summary = "리뷰 수정", description = "리뷰의 제목, 내용, 평점을 수정합니다.")
    @PutMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void change(
            @Parameter(description = "수정할 리뷰의 UUID") @PathVariable UUID reviewId,
            @Valid @RequestBody ReviewRequestDto.Change request) {

        reviewService.change(request.toServiceDto(reviewId));
    }

    @Operation(summary = "리뷰 삭제", description = "작성한 리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(
            @Parameter(description = "삭제할 리뷰의 UUID") @PathVariable UUID reviewId) {

        reviewService.remove(reviewId);
    }

    @Operation(summary = "리뷰 단건 상세 조회", description = "특정 리뷰의 상세 내용을 조회합니다.")
    @GetMapping("/{reviewId}")
    public ReviewResponseDto getReview(
            @Parameter(description = "조회할 리뷰의 UUID") @PathVariable UUID reviewId) {

        return ReviewResponseDto.from(reviewQueryService.getReview(reviewId));
    }

    @Operation(summary = "상점별 리뷰 목록 조회", description = "특정 상점에 달린 리뷰들을 검색 조건에 따라 조회합니다.")
    @GetMapping("/store/{storeId}")
    public Page<ReviewResponseDto> findAllByStore(
            @Parameter(description = "상점 UUID") @PathVariable UUID storeId,
            @ModelAttribute ReviewQueryDto.Search search,
            @PageableDefault(size = 10) Pageable pageable) {

        return reviewQueryService.getReviewsByStore(storeId, search, pageable)
                .map(ReviewResponseDto::from);
    }

    @Operation(summary = "사용자별 리뷰 목록 조회", description = "내가 작성한 리뷰 목록을 검색 조건에 따라 조회합니다.")
    @GetMapping("/user/{userId}")
    public Page<ReviewResponseDto> findAllByUser(
            @Parameter(description = "사용자 UUID") @PathVariable UUID userId,
            @ModelAttribute ReviewQueryDto.Search search,
            @PageableDefault(size = 10) Pageable pageable) {

        return reviewQueryService.getReviewsByUser(userId, search, pageable)
                .map(ReviewResponseDto::from);
    }

    @Operation(summary = "전체 리뷰 조회 (관리자용)", description = "시스템 내의 모든 리뷰를 검색 조건에 따라 조회합니다.")
    @GetMapping
    public Page<ReviewResponseDto> findAll(
            @ModelAttribute ReviewQueryDto.Search search,
            @PageableDefault(size = 10) Pageable pageable) {

        return reviewQueryService.getAllReviews(search, pageable)
                .map(ReviewResponseDto::from);
    }
}