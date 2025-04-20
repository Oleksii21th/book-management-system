package com.spring.book.management.service;

import com.spring.book.management.dto.BookDto;
import com.spring.book.management.dto.BookSearchParametersDto;
import com.spring.book.management.dto.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookDto save(CreateBookRequestDto createBookRequestDto);

    BookDto findById(Long id);

    List<BookDto> findAll(Pageable pageable);

    BookDto updateBook(Long id, CreateBookRequestDto updatedBook);

    void deleteBook(Long id);

    List<BookDto> search(BookSearchParametersDto searchParameters);
}
