package org.sparta.delivery.user.application;

public interface GenerateTokenService {
    TokenInfo generate(String username, String password);
}