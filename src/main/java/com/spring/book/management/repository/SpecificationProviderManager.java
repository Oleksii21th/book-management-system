package com.spring.book.management.repository;

import com.spring.book.management.repository.book.BookSpecificationProvider;

public interface SpecificationProviderManager<T> {
    BookSpecificationProvider<T> getSpecificationProvider(String key);
}
