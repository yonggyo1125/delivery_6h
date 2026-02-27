package org.sparta.delivery.global.infrastructure.api;

import org.sparta.delivery.global.domain.service.AddressToCoords;
import org.springframework.stereotype.Component;

@Component
public class KakaoAddressToCoords implements AddressToCoords {
    @Override
    public double[] convert(String address) {
        return new double[0];
    }
}
