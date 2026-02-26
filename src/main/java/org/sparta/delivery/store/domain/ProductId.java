package org.sparta.delivery.store.domain;

import jakarta.persistence.Embeddable;
import lombok.*;


@ToString
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductId {
    private StoreId storeId;
    private int productIdx;
}
