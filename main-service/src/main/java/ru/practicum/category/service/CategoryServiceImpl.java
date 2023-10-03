package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategory(newCategoryDto);
        getByName(category);
        Category newCategory = categoryRepository.save(CategoryMapper.toCategory(newCategoryDto));
        return CategoryMapper.toCategoryDto(newCategory);
    }

    @Override
    public CategoryDto getById(Long id) {
        return categoryRepository.findById(id)
                .map(CategoryMapper::toCategoryDto)
                .orElseThrow(() -> new NotFoundException("Категории с идентификатором " + id
                        + " нет в базе."));
    }

    @Override
    public CategoryDto update(Long id, NewCategoryDto newCategoryDto) {
        Category category = getCategory(id);
        if (category.getName().equals(newCategoryDto.getName())) {
            return CategoryMapper.toCategoryDto(category);
        }
        category.setName(newCategoryDto.getName());
        getByName(category);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void delete(Long catId) {
        Category category = getCategory(catId);
        if (eventRepository.existsByCategoryId(category.getId())) {
            throw new ConflictException("Категория с id = " + category.getId()
                    + " не может быть удалена из-за привязанных событий");
        }
        categoryRepository.deleteById(catId);
    }

    private void getByName(Category category) {
        if (categoryRepository.findByName(category.getName()) != null) {
            throw new ConflictException("Имя категории " + category.getName() + " уже существует.");
        }
    }

    private Category getCategory(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категории с идентификатором " + catId
                        + " нет в базе."));
    }

}
