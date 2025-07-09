package com.spring.book.management.dto.book;

public record BookSearchParametersDto(String[] titles, String[] authors, String[] isbns) {
}
