package org.sparta.delivery.global.presentation.exception;

import org.springframework.http.HttpStatusCode;

public record ErrorResponse(
        HttpStatusCode status,
        Object message
){}
