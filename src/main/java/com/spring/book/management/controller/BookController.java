package com.spring.book.management.controller;

import com.spring.book.management.dto.BookDto;
import com.spring.book.management.dto.BookSearchParametersDto;
import com.spring.book.management.dto.CreateBookRequestDto;
import com.spring.book.management.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Books", description = "Endpoints for managing books")
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Get all books",
            description = "Returns a list of all books with pagination support.")
    @GetMapping
    public List<BookDto> findAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @Operation(summary = "Get book by ID",
            description = "Returns a single book based on the provided ID")
    @GetMapping("/{id}")
    public BookDto findBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @Operation(summary = "Create a new book",
            description = "Creates a new book using the provided request body")
    @PostMapping
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto createBookRequestDto) {
        return bookService.save(createBookRequestDto);
    }

    @Operation(summary = "Update a book",
            description = "Updates the details of an existing book by ID")
    @PutMapping("/{id}")
    public BookDto updateBook(@PathVariable Long id,
                              @RequestBody @Valid CreateBookRequestDto createBookRequestDto) {
        return bookService.updateBook(id, createBookRequestDto);
    }

    @Operation(summary = "Delete a book",
            description = "Deletes a book by its ID")
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

    @Operation(summary = "Search books",
            description = "Searches for books using various parameters")
    @GetMapping("/search")
    public List<BookDto> search(@ModelAttribute BookSearchParametersDto searchParameters) {
        return bookService.search(searchParameters);
    }
}
