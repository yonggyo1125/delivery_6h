package org.sparta.delivery.store;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreId {

    @Column(length = 45, name="store_id")
    private UUID id;

    public static StoreId of() {
        return StoreId.of(UUID.randomUUID());
    }

    public static StoreId of(UUID id) {
        return new StoreId(id);
    }
}
