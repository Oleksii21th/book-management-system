package com.spring.book.management.mapper;

import com.spring.book.management.config.MapperConfig;
import com.spring.book.management.dto.CategoryDto;
import com.spring.book.management.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto dto);
}
