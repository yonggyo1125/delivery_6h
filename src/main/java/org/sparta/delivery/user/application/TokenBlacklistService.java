package org.sparta.delivery.user.application;

// 토큰 무효화 처리
public interface TokenBlacklistService {
    void invalidate(String token, long remainingTime);
}
