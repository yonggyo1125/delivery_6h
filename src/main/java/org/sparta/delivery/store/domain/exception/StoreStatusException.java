package org.sparta.delivery.store.domain.exception;

import org.sparta.delivery.global.domain.exception.BadRequestException;

public class StoreStatusException extends BadRequestException {

    public StoreStatusException() {
        super("폐업된 매장은 상태를 변경할 수 없습니다.");
    }
}
