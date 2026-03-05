package org.sparta.delivery.review.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewId {
    @Column(length = 45, name="review_id")
    private UUID id;

    public static ReviewId of() {
        return ReviewId.of(UUID.randomUUID());
    }

    public static ReviewId of(UUID id) {
        return new ReviewId(id);
    }
}
