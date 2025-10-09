package ru.practicum.ewm.category.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(NewCategoryDto dto);

    CategoryDto toDto(Category e);

    Category toCategory(CategoryDto categoryDto);
//    public static Category toEntity(NewCategoryDto dto) {
//        return Category.builder().name(dto.getName()).build();
//    }
//
//    public static CategoryDto toDto(Category e) {
//        return CategoryDto.builder().id(e.getId()).name(e.getName()).build();
//    }
//
//    public static Category toCategory(CategoryDto categoryDto) {
//        return Category.builder()
//                .id(categoryDto.getId())
//                .name(categoryDto.getName())
//                .build();
//    }
}