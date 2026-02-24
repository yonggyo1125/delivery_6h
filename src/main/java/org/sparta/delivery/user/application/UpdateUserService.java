package org.sparta.delivery.user.application;

import java.util.List;
import java.util.UUID;

public interface UpdateUserService {
    // 회원 정보 수정
    void update(UUID userId, UserUpdate dto);

    // 비밀번호 변경
    void updatePassword(UUID userId, String newPassword);

    // Role 변경
    void updateUserRole(UUID userId, List<String> roleNames);
}
