package org.sparta.delivery.store.domain.exception;

import org.sparta.delivery.global.domain.exception.BadRequestException;

public class ProductSubOptionDuplicatedException extends BadRequestException {
    public ProductSubOptionDuplicatedException() {
        super("이미 등록된 하위 옵션입니다.");
    }

    public ProductSubOptionDuplicatedException(String optionName) {
        super("[하위 옵션명:%s]이미 등록된 하위 옵션 입니다.".formatted(optionName));
    }
}
