package org.sparta.delivery.store.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class StoreNotFoundException extends HttpStatusCodeException {
    public StoreNotFoundException() {
        super(HttpStatus.NOT_FOUND, "매장을 찾을 수 없습니다.");
    }
}
