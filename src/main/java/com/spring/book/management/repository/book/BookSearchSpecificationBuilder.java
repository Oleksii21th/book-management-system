package com.spring.book.management.repository.book;

import com.spring.book.management.dto.book.BookSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface BookSearchSpecificationBuilder<T> {
    Specification<T> build(BookSearchParametersDto searchParameters);
}
