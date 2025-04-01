package com.spring.book.management.service;

import com.spring.book.management.dto.BookDto;
import com.spring.book.management.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {

    BookDto save(CreateBookRequestDto createBookRequestDto);

    BookDto findById(Long id);

    List<BookDto> findAll();

    BookDto updateBook(Long id, CreateBookRequestDto updatedBook);

    void deleteBook(Long id);
}
