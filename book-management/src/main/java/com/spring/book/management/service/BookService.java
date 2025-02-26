package com.spring.book.management.service;

import com.spring.book.management.model.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
