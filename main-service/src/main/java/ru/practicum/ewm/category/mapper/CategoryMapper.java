package ru.practicum.ewm.category.mapper;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.Category;

public class CategoryMapper {
    public static Category toEntity(NewCategoryDto dto) {
        return Category.builder().name(dto.getName()).build();
    }

    public static CategoryDto toDto(Category e) {
        return CategoryDto.builder().id(e.getId()).name(e.getName()).build();
    }
}