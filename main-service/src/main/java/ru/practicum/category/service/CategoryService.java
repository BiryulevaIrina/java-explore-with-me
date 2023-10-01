package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto getCategoryById(Long id);

    CategoryDto update(Long id, NewCategoryDto newCategoryDto);

    void delete(Long id);


}
