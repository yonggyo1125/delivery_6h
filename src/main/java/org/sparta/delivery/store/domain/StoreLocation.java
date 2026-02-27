package org.sparta.delivery.store.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreLocation {

    private String address;
    private double latitude; // 위도
    private double longitude; // 경도
}
