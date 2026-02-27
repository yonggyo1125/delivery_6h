package org.sparta.delivery.category.application;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.category.domain.Category;
import org.sparta.delivery.category.domain.CategoryRepository;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateCategoryService {
    private final RoleCheck roleCheck;
    private final CategoryRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void create(UUID id, String name) {

        Category category = Category.builder()
                .categoryId(id)
                .name(name)
                .roleCheck(roleCheck)
                .build();

        repository.save(category);
    }
}
