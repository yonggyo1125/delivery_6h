package org.sparta.delivery.store.domain;

import jakarta.persistence.Embeddable;
import lombok.*;
import org.sparta.delivery.global.domain.service.AddressToCoords;
import org.springframework.util.StringUtils;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreLocation {

    private String address;
    private double latitude; // 위도
    private double longitude; // 경도

    protected StoreLocation(String address, AddressToCoords addressToCoords) {
        this.address = address;
        if (!StringUtils.hasText(address)) return;

        double[] coords = addressToCoords.convert(address);
        latitude = coords[0];
        longitude = coords[1];

    }
}
