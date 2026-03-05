package org.sparta.delivery.review.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.sparta.delivery.global.domain.service.UserDetails;
import org.sparta.delivery.review.domain.exception.InvalidReviewerException;

import java.util.UUID;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reviewer {
    @Column(length=45, name="reviewer_id")
    private UUID id;

    @Column(length=45)
    private String reviewerName;

    protected Reviewer(UserDetails userDetails) {
        if (userDetails == null || userDetails.getId() == null) {
            throw new InvalidReviewerException();
        }

        this.id = userDetails.getId();
        this.reviewerName = userDetails.getName();
    }
}
