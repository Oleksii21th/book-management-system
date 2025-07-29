package com.spring.book.management.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryDto(
        Long id,
        @NotBlank(message = "Name must not be blank") String name,
        String description) {
}
