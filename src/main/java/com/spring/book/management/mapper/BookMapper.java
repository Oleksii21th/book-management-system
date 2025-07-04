package com.spring.book.management.mapper;

import com.spring.book.management.config.MapperConfig;
import com.spring.book.management.dto.BookDto;
import com.spring.book.management.dto.BookDtoWithoutCategoryIds;
import com.spring.book.management.dto.CreateBookRequestDto;
import com.spring.book.management.model.Book;
import com.spring.book.management.model.Category;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Book toModel(CreateBookRequestDto createBookRequestDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @Mapping(target = "id", ignore = true)
    void toEntity(CreateBookRequestDto dto, @MappingTarget Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto dto, Book book) {
        if (book.getCategories() != null) {
            dto.setCategoryIds(book.getCategories()
                            .stream()
                            .map(Category::getId)
                            .collect(Collectors.toSet()));
        }
    }
}
