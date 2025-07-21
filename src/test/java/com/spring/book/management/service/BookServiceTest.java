package com.spring.book.management.service;

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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    @DisplayName("Verify method save works correctly")
    void save_ValidDto_ReturnsBookDto() {
        // given
        CreateBookRequestDto dto = createBookRequestDto();
        Book book = new Book();
        Book savedBook = new Book();
        BookDto bookDto = new BookDto();
        List<Category> categories = new ArrayList<>();

        when(bookMapper.toModel(dto)).thenReturn(book);
        when(categoryRepository.findAllById(dto.getCategoryIds())).thenReturn(categories);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.toDto(savedBook)).thenReturn(bookDto);

        // when
        BookDto savedBookDto = bookService.save(dto);

        // then
        assertThat(savedBookDto).isEqualTo(bookDto);
        assertNotNull(savedBookDto);
        verify(bookRepository, times(1)).save(book);
        verify(categoryRepository).findAllById(Set.of(1L));
    }

    @Test
    @DisplayName("Saves book when categoryIds is null")
    void save_NullCategoryIds_SavesBook() {
        CreateBookRequestDto dto = createBookRequestDto();
        dto.setCategoryIds(null);
        Book book = new Book();
        Book savedBook = new Book();
        BookDto bookDto = new BookDto();

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
        // given
        Book book = new Book();
        book.setId(1L);
        BookDto bookDto = new BookDto();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        // when
        BookDto result = bookService.findById(1L);

        // then
        assertNotNull(result);
        verify(bookRepository).findById(1L);
    }

    @Test
    @DisplayName("Throws exception if a book with the given ID does not exist")
    void findById_NonExistingId_ThrowsBookNotFoundException() {
        // when
        when(bookRepository.findById(100L)).thenReturn(Optional.empty());
        // then
        assertThrows(BookNotFoundException.class, () -> bookService.findById(100L));
    }

    @Test
    @DisplayName("Returns a list of BookDto for the given page request")
    void findAll_ValidPageable_ReturnsListOfBookDto() {
        // given
        Book book = new Book();
        BookDto bookDto = new BookDto();
        Pageable pageable = PageRequest.of(0, 10);

        when(bookRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(book)));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        // when
        List<BookDto> result = bookService.findAll(pageable);

        // then
        assertEquals(1, result.size());
        verify(bookRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Updates an existing book and returns the updated BookDto")
    void updateBook_ExistingId_UpdatesAndReturnsBookDto() {
        // given
        Long bookId = 1L;
        CreateBookRequestDto dto = createBookRequestDto();

        Book book = new Book();
        Book updatedBook = new Book();
        BookDto updatedDto = new BookDto();

        when(categoryRepository.findAllById(dto.getCategoryIds())).thenReturn(List.of(new Category()));
        mockUpdatedBook(dto, book, updatedBook, updatedDto);

        // when
        BookDto result = bookService.updateBook(bookId, dto);

        // then
        assertNotNull(result);
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("Updates an existing book without category")
    void updateBook_NullCategoryIds_UpdatesAndReturnsBookDto() {
        // given
        Long bookId = 1L;
        CreateBookRequestDto dto = createBookRequestDto();
        dto.setCategoryIds(null);

        Book book = new Book();
        Book updatedBook = new Book();
        BookDto updatedDto = new BookDto();

        mockUpdatedBook(dto, book, updatedBook, updatedDto);

        // when
        BookDto result = bookService.updateBook(bookId, dto);

        // then
        assertNotNull(result);
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("Deletes a book with the given ID")
    void deleteBook_ValidId_CallsRepositoryDeleteById() {
        // when
        bookService.deleteBook(1L);

        // then
        verify(bookRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Returns a list of books matching the search parameters")
    void search_ValidParameters_ReturnsMatchingBookDto() {
        // given
        BookSearchParametersDto params = new BookSearchParametersDto(
                new String[]{"test1", "test2"},
                new String[]{"test1"},
                new String[]{"1"});
        Book book = new Book();
        BookDto bookDto = new BookDto();
        Specification<Book> specification =
                (root, query, cb) -> null;

        when(bookSpecificationBuilder.build(params)).thenReturn(specification);
        when(bookRepository.findAll(specification)).thenReturn(List.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        // when
        List<BookDto> result = bookService.search(params);

        // then
        assertEquals(1, result.size());
        verify(bookSpecificationBuilder).build(params);
        verify(bookRepository).findAll(specification);
        verify(bookMapper).toDto(book);
    }

    private CreateBookRequestDto createBookRequestDto() {
        CreateBookRequestDto dto = new CreateBookRequestDto(
                "test",
                "test",
                "ISBN",
                BigDecimal.ONE,
                "test",
                null);
        dto.setCategoryIds(Set.of(1L));
        return dto;
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
