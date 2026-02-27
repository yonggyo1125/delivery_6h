package org.sparta.delivery.store.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class ProductNotFoundException extends HttpStatusCodeException {
    public ProductNotFoundException() {
        super(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다.");
    }
}
