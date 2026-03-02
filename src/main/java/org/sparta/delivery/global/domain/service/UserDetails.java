package org.sparta.delivery.global.domain.service;

import java.util.UUID;

// 인증한 사용자의 정보 조회
public interface UserDetails {
    UUID getId();
    String getName();
    String getEmail();
    String getMobile();
    boolean isAuthenticated(); // 로그인 여부
}
