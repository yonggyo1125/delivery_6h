package org.sparta.delivery.category.application;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.category.application.dto.CategoryServiceDto;
import org.sparta.delivery.category.domain.Category;
import org.sparta.delivery.category.domain.CategoryId;
import org.sparta.delivery.category.domain.CategoryRepository;
import org.sparta.delivery.category.domain.exception.CategoryNotFoundException;
import org.sparta.delivery.global.domain.exception.BadRequestException;
import org.sparta.delivery.global.domain.service.RoleCheck;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final RoleCheck roleCheck;
    private final CategoryRepository repository;

    @Transactional
    public List<UUID> create(List<CategoryServiceDto.Category> categories) {
        if (categories == null || categories.isEmpty()) return List.of();

        List<Category> items = categories.stream().map(this::toCategory).toList();

        return repository.saveAll(items).stream()
                .map(c -> c.getId().getId()).toList();
    }

    @Transactional
    public void change(List<CategoryServiceDto.Category> categories) {
        if (categories == null || categories.isEmpty()) {
            throw new BadRequestException("수정할 카테고리 정보가 누락되었습니다.");
        }

        Map<UUID, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(CategoryServiceDto.Category::getId, CategoryServiceDto.Category::getName));

        List<Category> items = getCategories(new ArrayList<>(categoryMap.keySet()));

        items.forEach(item -> {
            String newName = categoryMap.get(item.getId().getId());
            if (newName != null) {
                item.change(newName, roleCheck);
            }
        });

    }

    @Transactional
    public void remove(List<UUID> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) return;
        List<Category> items = getCategories(categoryIds);
        items.forEach(item -> item.remove(roleCheck));
    }

    // 카테고리 조회
    private List<Category> getCategories(List<UUID> categoryIds) {
        List<CategoryId> ids = categoryIds.stream().map(CategoryId::of).toList();
        List<Category> items = repository.findAllById(ids);

        // 요청한 개수와 DB에서 찾은 개수가 다르면 등록되지 않은 ID가 포함된 것임
        if (items.size() != categoryIds.size()) {
            throw new CategoryNotFoundException("일부 카테고리가 존재하지 않거나 이미 삭제되었습니다.");
        }

        return items;
    }

    private Category toCategory(CategoryServiceDto.Category category) {
        return Category.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .roleCheck(roleCheck)
                .build();
    }
}
