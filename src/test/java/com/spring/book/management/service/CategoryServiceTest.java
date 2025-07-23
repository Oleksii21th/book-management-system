package com.spring.book.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.spring.book.management.dto.CategoryDto;
import com.spring.book.management.dto.book.BookDtoWithoutCategoryIds;
import com.spring.book.management.exception.CategoryNotFoundException;
import com.spring.book.management.mapper.BookMapper;
import com.spring.book.management.mapper.CategoryMapper;
import com.spring.book.management.model.Book;
import com.spring.book.management.model.Category;
import com.spring.book.management.repository.book.BookRepository;
import com.spring.book.management.repository.category.CategoryRepository;
import com.spring.book.management.service.impl.CategoryServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Test");
        category.setDescription("Test");

        categoryDto = new CategoryDto(1L, "Test", "Test");
    }

    @Test
    @DisplayName("Returns all categories as DTOs")
    void findAll_ReturnsListOfCategoryDto() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        List<CategoryDto> result = categoryService.findAll();

        assertThat(result).hasSize(1).containsExactly(categoryDto);
        verify(categoryRepository).findAll();
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("Returns category by ID")
    void getById_ExistingId_ReturnsCategoryDto() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.getById(1L);

        assertThat(result).isEqualTo(categoryDto);
        verify(categoryRepository).findById(1L);
    }

    @Test
    @DisplayName("Throws CategoryNotFoundException if ID not found")
    void getById_NonExistingId_ThrowsException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.getById(99L));
        verify(categoryRepository).findById(99L);
    }

    @Test
    @DisplayName("Saves category and returns DTO")
    void save_ValidDto_ReturnsSavedDto() {
        when(categoryMapper.toEntity(categoryDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.save(categoryDto);

        assertThat(result).isEqualTo(categoryDto);
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Updates existing category and returns DTO")
    void update_ExistingId_UpdatesAndReturnsDto() {
        CategoryDto updatedDto = new CategoryDto(
                1L,
                "Updated",
                "Updated");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any())).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(updatedDto);

        CategoryDto result = categoryService.update(1L, updatedDto);

        assertThat(result).isEqualTo(updatedDto);
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Throws exception when updating non-existing category")
    void update_NonExistingId_ThrowsException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.update(99L, categoryDto));
        verify(categoryRepository).findById(99L);
    }

    @Test
    @DisplayName("Deletes category by ID")
    void deleteById_ValidId_DeletesCategory() {
        categoryService.deleteById(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Returns books by category ID")
    void getBooksByCategoryId_ValidId_ReturnsBookDtoList() {
        Book book = new Book();
        BookDtoWithoutCategoryIds bookDto = new BookDtoWithoutCategoryIds(
                1L,
                "Test",
                null,
                "Test",
                BigDecimal.ONE,
                null,
                null
        );

        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(bookRepository.findByCategories_Id(1L)).thenReturn(List.of(book));
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDto);

        List<BookDtoWithoutCategoryIds> result = categoryService.getBooksByCategoryId(1L);

        assertThat(result).hasSize(1).containsExactly(bookDto);
        verify(categoryRepository).existsById(1L);
        verify(bookRepository).findByCategories_Id(1L);
        verify(bookMapper).toDtoWithoutCategories(book);
    }

    @Test
    @DisplayName("Throws exception when getting books for non-existing category")
    void getBooksByCategoryId_NonExistingId_ThrowsException() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.getBooksByCategoryId(99L));
        verify(categoryRepository).existsById(99L);
    }
}
