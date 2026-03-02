package org.sparta.delivery.category.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.sparta.delivery.global.domain.BaseUserEntity;
import org.sparta.delivery.global.domain.exception.UnAuthorizedException;
import org.sparta.delivery.global.domain.service.RoleCheck;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 카테고리는 관리자(MANAGER, MASTER)만 가능해야 한다.
 *
 */
@Getter
@ToString
@Entity
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseUserEntity {

    @EmbeddedId
    private CategoryId id;

    private String name; // 카테고리명

    @Builder
    public Category(UUID categoryId, String name, RoleCheck roleCheck) {
        // 권한 체크
        checkAuthority(roleCheck);

        this.id = categoryId == null ? CategoryId.of():CategoryId.of(categoryId);
        this.name = name;
    }

    private void checkAuthority(RoleCheck roleCheck) {
        if (!roleCheck.hasRole(List.of("MANAGER", "MASTER"))) {
            throw new UnAuthorizedException();
        }
    }

    // 카테고리 변경
    public void change(String name, RoleCheck roleCheck) {
        checkAuthority(roleCheck);

        this.name = name;
    }

    // 카테고리 제거
    public void remove(RoleCheck roleCheck) {
        checkAuthority(roleCheck);

        deletedAt = LocalDateTime.now();
    }
}
