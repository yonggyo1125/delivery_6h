package org.sparta.delivery.category.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sparta.delivery.category.application.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/category")
public class CategoryController {
    private final CategoryService createService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201
    public void create(@RequestBody @Valid CategoryRequest req) {

        createService.create(req.id(), req.name());
    }
}
