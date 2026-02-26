package org.sparta.delivery.store.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreCategory {

    @Column(length=45, name="category_id", nullable = false)
    private UUID categoryId;
}
