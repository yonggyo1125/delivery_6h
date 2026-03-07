package org.sparta.delivery.review.presentation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import org.sparta.delivery.review.application.ReviewServiceDto;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewRequestDto {

    @Data
    @Schema(description = "리뷰 생성 요청")
    public static class Create {

        @Schema(description = "주문 ID", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "주문 번호는 필수 항목입니다.")
        private UUID orderId;

        @Schema(description = "리뷰 제목", example = "정말 맛있어요!", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "리뷰 제목을 입력해 주세요.")
        @Size(max = 200, message = "제목은 최대 200자까지 입력 가능합니다.")
        private String subject;

        @Schema(description = "리뷰 내용 (선택 항목)", example = "배달도 빠르고 음식도 따뜻합니다.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String content;

        @Schema(description = "평점 (1~5점)", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
        @Min(value = 1, message = "평점은 1점에서 5점 사이여야 합니다.")
        @Max(value = 5, message = "평점은 1점에서 5점 사이여야 합니다.")
        private int score;

        public ReviewServiceDto.Create toServiceDto() {
            return ReviewServiceDto.Create.builder()
                    .orderId(orderId)
                    .subject(subject)
                    .content(content)
                    .score(score)
                    .build();
        }
    }

    @Data
    @Schema(description = "리뷰 수정 요청")
    public static class Change {

        @Schema(description = "수정할 제목", example = "수정된 제목입니다.", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "리뷰 제목을 입력해 주세요.")
        private String subject;

        @Schema(description = "수정할 내용 (선택 항목)", example = "내용을 수정해 보았습니다.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String content;

        @Schema(description = "수정할 평점 (1~5점)", example = "4", requiredMode = Schema.RequiredMode.REQUIRED)
        @Min(value = 1, message = "평점은 1점에서 5점 사이여야 합니다.")
        @Max(value = 5, message = "평점은 1점에서 5점 사이여야 합니다.")
        private int score;

        public ReviewServiceDto.Change toServiceDto(UUID reviewId) {
            return ReviewServiceDto.Change.builder()
                    .reviewId(reviewId)
                    .subject(subject)
                    .content(content)
                    .score(score)
                    .build();
        }
    }
}
