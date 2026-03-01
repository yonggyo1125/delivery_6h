package org.sparta.delivery.store.domain.exception;

import org.sparta.delivery.global.domain.exception.BadRequestException;

public class InvalidAddressException extends BadRequestException {
    public InvalidAddressException(String address) {
        super("유효하지 않은 주소이거나 좌표 변환에 실패했습니다:(%s)".formatted(address));
    }
}
