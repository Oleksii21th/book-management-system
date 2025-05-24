package com.spring.book.management.dto;

public class UserLoginResponseDto {
    private final String jwtToken;

    public UserLoginResponseDto(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getToken() {
        return jwtToken;
    }
}
