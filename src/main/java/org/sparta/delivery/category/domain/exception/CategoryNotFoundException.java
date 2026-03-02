package org.sparta.delivery.category.domain.exception;

import org.sparta.delivery.global.domain.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {
    public CategoryNotFoundException(String message) {
        super(message);
    }
    public CategoryNotFoundException() {
        this("카테고리를 찾을 수 없습니다.");
    }
}
