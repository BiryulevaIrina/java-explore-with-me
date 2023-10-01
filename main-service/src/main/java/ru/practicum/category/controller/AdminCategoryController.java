package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
@Slf4j
@Validated
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createNewCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Получен POST-запрос на добавление категории");
        return categoryService.create(newCategoryDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@Min(1) @PathVariable Long id,
                                      @Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Получен PATCH-запрос на изменение категории c id={}", id);
        return categoryService.update(id, newCategoryDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Min(1) @PathVariable Long id) {
        log.info("Получен DELETE-запрос на удаление категории с ID={}", id);
        categoryService.delete(id);
    }
}
