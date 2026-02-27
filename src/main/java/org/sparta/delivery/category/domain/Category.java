package org.sparta.delivery.category.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.*;
import org.sparta.delivery.global.domain.BaseUserEntity;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.sparta.delivery.global.presentation.exception.UnAuthorizedException;

import java.util.List;
import java.util.UUID;

/**
 * 1. 카테고리는 관리자(MANAGER, MASTER)만 가능해야 한다.
 * 2. 카테고리는 이름만 변경 가능하고 새로운 카테고리 객체를 반환
 */

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseUserEntity {

    @EmbeddedId
    private CategoryId id;

    private String name; // 카테고리명


    @Builder
    public Category(UUID categoryId, String name, RoleCheck roleCheck) {
        // 권한 체크
        checkPossible(roleCheck);

        this.id = categoryId == null ? CategoryId.of():CategoryId.of(categoryId);
        this.name = name;
    }

    private void checkPossible(RoleCheck roleCheck) {
        if (!roleCheck.hasRole(List.of("MANAGER", "MASTER"))) {
            throw new UnAuthorizedException();
        }
    }

    public Category changeName(String name, RoleCheck roleCheck) {

        return new Category(id.getId(), name, roleCheck);
    }
}
