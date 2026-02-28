package org.sparta.delivery.store.domain.exception;

import org.sparta.delivery.global.presentation.exception.BadRequestException;

public class ProductDuplicatedException extends BadRequestException {
    public ProductDuplicatedException() {
        super("이미 등록된 상품입니다.");
    }

    public ProductDuplicatedException(String productCode) {
        super("[상품코드:%s]이미 등록된 상품입니다.".formatted(productCode));
    }
}
