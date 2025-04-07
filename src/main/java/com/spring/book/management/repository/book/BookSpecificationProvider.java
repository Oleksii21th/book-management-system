package com.spring.book.management.repository.book;

import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.domain.Specification;

public interface BookSpecificationProvider<T> {
    String getKey();

    Specification<T> getSpecification(Map<String, List<String>> params);
}
