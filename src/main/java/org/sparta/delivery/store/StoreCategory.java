package org.sparta.delivery.store;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.sparta.delivery.global.domain.BaseUserEntity;

import java.util.UUID;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreCategory extends BaseUserEntity {

    @Column(length=45, name="category_id", nullable = false)
    private UUID categoryId;
}
