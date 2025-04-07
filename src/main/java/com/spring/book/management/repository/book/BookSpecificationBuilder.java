package com.spring.book.management.repository.book;

import com.spring.book.management.dto.BookSearchParametersDto;
import com.spring.book.management.model.Book;
import com.spring.book.management.repository.SpecificationProviderManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BookSpecificationBuilder implements BookSearchSpecificationBuilder<Book> {

    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    public BookSpecificationBuilder(
            SpecificationProviderManager<Book> bookSpecificationProviderManager) {
        this.bookSpecificationProviderManager = bookSpecificationProviderManager;
    }

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParameters) {
        Map<String, List<String>> searchParams = extractParams(searchParameters);

        Specification<Book> spec = Specification.where(null);

        for (Map.Entry<String, List<String>> entry : searchParams.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            if (values != null && !values.isEmpty()) {
                BookSpecificationProvider<Book> provider =
                        bookSpecificationProviderManager.getSpecificationProvider(key);
                Specification<Book> newSpec = provider.getSpecification(Map.of(key, values));
                if (newSpec != null) {
                    spec = spec.and(newSpec);
                }
            }
        }

        return spec;
    }

    private Map<String, List<String>> extractParams(BookSearchParametersDto dto) {
        Map<String, List<String>> paramMap = new HashMap<>();

        if (dto.titles() != null) {
            paramMap.put("title", Arrays.asList(dto.titles()));
        }
        if (dto.authors() != null) {
            paramMap.put("author", Arrays.asList(dto.authors()));
        }
        if (dto.isbns() != null) {
            paramMap.put("isbn", Arrays.asList(dto.isbns()));
        }

        return paramMap;
    }
}
