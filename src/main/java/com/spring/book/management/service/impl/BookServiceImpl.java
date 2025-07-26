package com.spring.book.management.service.impl;

import com.spring.book.management.dto.book.BookDto;
import com.spring.book.management.dto.book.BookSearchParametersDto;
import com.spring.book.management.dto.book.CreateBookRequestDto;
import com.spring.book.management.exception.BookNotFoundException;
import com.spring.book.management.mapper.BookMapper;
import com.spring.book.management.model.Book;
import com.spring.book.management.model.Category;
import com.spring.book.management.repository.book.BookRepository;
import com.spring.book.management.repository.book.BookSpecificationBuilder;
import com.spring.book.management.repository.category.CategoryRepository;
import com.spring.book.management.service.BookService;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    public BookServiceImpl(BookRepository bookRepository,
                           CategoryRepository categoryRepository,
                           BookMapper bookMapper,
                           BookSpecificationBuilder bookSpecificationBuilder) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.bookMapper = bookMapper;
        this.bookSpecificationBuilder = bookSpecificationBuilder;
    }

    public BookDto save(CreateBookRequestDto dto) {
        Book book = bookMapper.toModel(dto);
        setBookCategories(book, dto.getCategoryIds());
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    @Override
    public BookDto findById(Long id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isEmpty()) {
            throw new BookNotFoundException(id);
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

    public BookDto updateBook(Long id, CreateBookRequestDto dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));
        bookMapper.toEntity(dto, book);

        setBookCategories(book, dto.getCategoryIds());
        Book updatedBook = bookRepository.save(book);
        return bookMapper.toDto(updatedBook);
    }

    @Override
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book not found with id: " + id);
        }
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

    private void setBookCategories(Book book, Set<Long> categoryIds) {
        if (categoryIds != null && !categoryIds.isEmpty()) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(categoryIds));
            book.setCategories(categories);
        }
    }
}
