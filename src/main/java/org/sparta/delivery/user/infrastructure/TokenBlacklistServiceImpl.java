package org.sparta.delivery.user.infrastructure;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.user.application.TokenBlacklistService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    //
    @Override
    public void invalidate(String token, long time) {

    }
}
