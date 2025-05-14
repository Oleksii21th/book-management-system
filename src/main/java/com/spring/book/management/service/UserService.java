package com.spring.book.management.service;

import com.spring.book.management.dto.UserRegistrationRequestDto;
import com.spring.book.management.dto.UserResponseDto;

public interface UserService {
    UserResponseDto registerUser(UserRegistrationRequestDto request);
}
