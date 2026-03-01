package org.sparta.delivery.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@ToString
@Getter
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreInfo {
    @Column(nullable = false)
    private UUID storeId;

    @Column(length = 65, nullable = false)
    private String storeName;

    @Column(length = 100, nullable = false)
    private String storeAddress;

    @Column(length = 30, nullable = false)
    private String storeTel;
}
