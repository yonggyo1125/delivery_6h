package org.sparta.delivery.category.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryId {
    @Column(length = 45, name="category_id")
    private UUID id;

    public static CategoryId of() {
        return CategoryId.of(UUID.randomUUID());
    }

    public static CategoryId of(UUID id) {
        return new CategoryId(id);
    }
}
