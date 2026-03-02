package org.sparta.delivery.store.domain.exception;

import org.sparta.delivery.global.domain.exception.BadRequestException;

public class ProductOptionDuplicatedException extends BadRequestException  {
    public ProductOptionDuplicatedException() {
        super("이미 등록된 옵션입니다.");
    }

    public ProductOptionDuplicatedException(String optionName) {
        super("[옵션명:%s]이미 등록된 옵션 입니다.".formatted(optionName));
    }
}
