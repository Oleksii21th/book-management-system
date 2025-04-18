package com.spring.book.management.repository.book;

import com.spring.book.management.exception.SpecificationNotFoundException;
import com.spring.book.management.model.Book;
import com.spring.book.management.repository.SpecificationProviderManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BookSpecificationBuilderManager implements SpecificationProviderManager<Book> {

    private final List<BookSpecificationProvider<Book>> specificationProviders;

    public BookSpecificationBuilderManager(
            List<BookSpecificationProvider<Book>> specificationProviders) {
        this.specificationProviders = specificationProviders;
    }

    @Override
    public BookSpecificationProvider<Book> getSpecificationProvider(String key) {
        return specificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new SpecificationNotFoundException(key));
    }
}
