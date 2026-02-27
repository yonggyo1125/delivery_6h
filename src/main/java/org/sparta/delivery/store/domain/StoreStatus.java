package org.sparta.delivery.store.domain;

public enum StoreStatus {
    PREPARING, // 오픈 준비중
    OPEN, // 운영중
    CLOSED, // 휴업중
    DEFUNCT // 폐업
}
