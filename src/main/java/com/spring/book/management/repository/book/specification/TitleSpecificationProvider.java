package com.spring.book.management.repository.book.specification;

import com.spring.book.management.model.Book;
import com.spring.book.management.repository.book.BookSpecificationProvider;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements BookSpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "title";
    }

    @Override
    public Specification<Book> getSpecification(Map<String, List<String>> params) {
        List<String> titles = params.get(getKey());

        if (titles == null || titles.isEmpty()) {
            return null;
        }

        return (root, query, cb) -> root.get("title").in(titles);
    }
}
