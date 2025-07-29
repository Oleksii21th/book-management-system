package com.spring.book.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.spring.book.management.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;

    private CreateBookRequestDto dto;
    private Book book;
    private Book savedBook;
    private BookDto bookDto;
    private Book updatedBook;
    private BookDto updatedDto;

    @BeforeEach
    void setUp() {
        dto = new CreateBookRequestDto(
                "test",
                "test",
                "ISBN",
                BigDecimal.ONE,
                "test",
                null);
        dto.setCategoryIds(Set.of(1L));

        book = new Book();
        savedBook = new Book();
        updatedBook = new Book();

        bookDto = new BookDto();
        updatedDto = new BookDto();
    }

    @Test
    @DisplayName("Verify method save works correctly")
    void save_ValidDto_ReturnsBookDto() {
        List<Category> categories = new ArrayList<>();

        when(bookMapper.toModel(dto)).thenReturn(book);
        when(categoryRepository.findAllById(dto.getCategoryIds())).thenReturn(categories);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.toDto(savedBook)).thenReturn(bookDto);

        BookDto savedBookDto = bookService.save(dto);

        assertThat(savedBookDto).isEqualTo(bookDto);
        assertNotNull(savedBookDto);
        verify(bookRepository, times(1)).save(book);
        verify(categoryRepository).findAllById(Set.of(1L));
    }

    @Test
    @DisplayName("Saves book when categoryIds is null")
    void save_NullCategoryIds_SavesBook() {
        dto.setCategoryIds(null);

        when(bookMapper.toModel(dto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.toDto(savedBook)).thenReturn(bookDto);

        BookDto result = bookService.save(dto);

        assertNotNull(result);
        verify(bookRepository).save(book);
        verify(categoryRepository, times(0)).findAllById(any());
    }

    @Test
    @DisplayName("Returns BookDto if a book with the given ID exists")
    void findById_ExistingId_ReturnsBookDto() {
        book.setId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto result = bookService.findById(1L);

        assertNotNull(result);
        verify(bookRepository).findById(1L);
    }

    @Test
    @DisplayName("Throws exception if a book with the given ID does not exist")
    void findById_NonExistingId_ThrowsBookNotFoundException() {
        when(bookRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.findById(100L));
    }

    @Test
    @DisplayName("Returns a list of BookDto for the given page request")
    void findAll_ValidPageable_ReturnsListOfBookDto() {
        Pageable pageable = PageRequest.of(0, 10);

        when(bookRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(book)));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.findAll(pageable);

        assertEquals(1, result.size());
        verify(bookRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Updates an existing book and returns the updated BookDto")
    void updateBook_ExistingId_UpdatesAndReturnsBookDto() {
        when(categoryRepository.findAllById(dto.getCategoryIds()))
                .thenReturn(List.of(new Category()));
        mockUpdatedBook(dto, book, updatedBook, updatedDto);

        BookDto result = bookService.updateBook(1L, dto);

        assertNotNull(result);
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("Updates an existing book without category")
    void updateBook_NullCategoryIds_UpdatesAndReturnsBookDto() {
        dto.setCategoryIds(null);
        mockUpdatedBook(dto, book, updatedBook, updatedDto);

        BookDto result = bookService.updateBook(1L, dto);

        assertNotNull(result);
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("Deletes a book with the given ID")
    void deleteBook_ValidId_CallsRepositoryDeleteById() {
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(true);

        bookService.deleteBook(bookId);

        verify(bookRepository).existsById(bookId);
        verify(bookRepository).deleteById(bookId);
    }

    @Test
    @DisplayName("Returns a list of books matching the search parameters")
    void search_ValidParameters_ReturnsMatchingBookDto() {
        BookSearchParametersDto params = new BookSearchParametersDto(
                new String[]{"test1", "test2"},
                new String[]{"test1"},
                new String[]{"1"});
        Specification<Book> specification = (root, query, cb) -> null;

        when(bookSpecificationBuilder.build(params)).thenReturn(specification);
        when(bookRepository.findAll(specification)).thenReturn(List.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.search(params);

        assertEquals(1, result.size());
        verify(bookSpecificationBuilder).build(params);
        verify(bookRepository).findAll(specification);
        verify(bookMapper).toDto(book);
    }

    private void mockUpdatedBook(CreateBookRequestDto dto,
                                 Book book,
                                 Book updatedBook,
                                 BookDto updatedDto) {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        doNothing().when(bookMapper).toEntity(dto, book);
        when(bookRepository.save(book)).thenReturn(updatedBook);
        when(bookMapper.toDto(updatedBook)).thenReturn(updatedDto);
    }
}
