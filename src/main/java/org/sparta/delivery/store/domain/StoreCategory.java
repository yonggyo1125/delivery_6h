package org.sparta.delivery.store.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.sparta.delivery.global.domain.BaseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreCategory extends BaseEntity {

    @Column(length=45, name="category_id", nullable = false)
    private UUID categoryId;

    // Soft Delete
    protected void remove() {
        deletedAt = LocalDateTime.now();
    }
}
