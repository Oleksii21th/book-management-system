package com.spring.book.management.dto;

public record BookSearchParametersDto(String[] titles, String[] authors, String[] isbns) {
}
