package org.sparta.delivery.global.domain.service;

// 주소를 좌표로 변환
public interface AddressToCoords {
    double[] convert(String address);
}
