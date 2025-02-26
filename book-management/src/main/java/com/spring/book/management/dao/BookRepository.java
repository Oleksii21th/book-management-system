package com.spring.book.management.dao;

import com.spring.book.management.model.Book;
import java.util.List;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
