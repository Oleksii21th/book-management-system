package com.spring.book.management.repository.book;

import com.spring.book.management.dto.BookSearchParametersDto;
import com.spring.book.management.model.Book;
import com.spring.book.management.repository.SpecificationBuilder;
import com.spring.book.management.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {

    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    public BookSpecificationBuilder(
            SpecificationProviderManager<Book> bookSpecificationProviderManager) {
        this.bookSpecificationProviderManager = bookSpecificationProviderManager;
    }

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParameters) {
        Specification<Book> spec = Specification.where(null);

        spec = addSpecificationIfPresent(spec, "title", searchParameters.titles());
        spec = addSpecificationIfPresent(spec, "author", searchParameters.authors());
        spec = addSpecificationIfPresent(spec, "isbn", searchParameters.isbns());

        return spec;
    }

    private Specification<Book> addSpecificationIfPresent(Specification<Book> spec,
                                                          String key,
                                                          String[] values) {
        if (values != null && values.length > 0) {
            return spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider(key)
                    .getSpecification(values));
        }
        return spec;
    }
}
