package com.spring.book.management.service.impl;

import com.spring.book.management.dto.BookDto;
import com.spring.book.management.dto.BookSearchParametersDto;
import com.spring.book.management.dto.CreateBookRequestDto;
import com.spring.book.management.exception.EntityNotFoundException;
import com.spring.book.management.mapper.BookMapper;
import com.spring.book.management.model.Book;
import com.spring.book.management.repository.book.BookRepository;
import com.spring.book.management.repository.book.BookSpecificationBuilder;
import com.spring.book.management.service.BookService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    public BookServiceImpl(BookRepository bookRepository,
                           BookMapper bookMapper,
                           BookSpecificationBuilder bookSpecificationBuilder) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.bookSpecificationBuilder = bookSpecificationBuilder;
    }

    @Override
    public BookDto save(CreateBookRequestDto createBookRequestDto) {
        Book book = bookMapper.toModel(createBookRequestDto);
        Book savedBook = bookRepository.save(book);

        return bookMapper.toDto(savedBook);
    }

    @Override
    public BookDto findById(Long id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isEmpty()) {
            throw new EntityNotFoundException(id);
        }

        return bookOptional.map(bookMapper::toDto).orElse(null);
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);

        return books.stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookDto updateBook(Long id, CreateBookRequestDto updatedBook) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));

        bookMapper.toEntity(updatedBook, book);
        Book savedBook = bookRepository.save(book);

        return bookMapper.toDto(savedBook);
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> search(BookSearchParametersDto searchParameters) {
        Specification<Book> bookSpecification =
                bookSpecificationBuilder.build(searchParameters);
        return bookRepository.findAll(bookSpecification)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
