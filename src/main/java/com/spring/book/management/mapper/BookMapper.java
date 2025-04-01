package com.spring.book.management.mapper;

import com.spring.book.management.config.MapperConfig;
import com.spring.book.management.dto.BookDto;
import com.spring.book.management.dto.CreateBookRequestDto;
import com.spring.book.management.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {

    BookDto toDto(Book book);

    @Mapping(target = "id", ignore = true)
    Book toModel(CreateBookRequestDto createBookRequestDto);

    @Mapping(target = "id", ignore = true)
    void mapToExistingEntity(CreateBookRequestDto dto, @MappingTarget Book book);
}
