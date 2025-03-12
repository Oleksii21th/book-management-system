package com.spring.book.management.dao.impl;

import com.spring.book.management.dao.BookRepository;
import com.spring.book.management.model.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepositoryImpl implements BookRepository {

    private final List<Book> bookDatabase = new ArrayList<>();

    @Override
    public Book save(Book book) {
        long id = bookDatabase.size() + 1;
        book.setId(id);
        bookDatabase.add(book);
        return book;
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookDatabase.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(bookDatabase);
    }
}
