package org.sparta.delivery.user.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TokenRequest(
        @NotBlank
        @Schema(name="username", title="로그인 아이디", example = "user004", format = "string")
        String username,

        @NotBlank
        @Schema(title="로그인 비밀번호", example = "_aA123456", format = "string")
        String password
) {}