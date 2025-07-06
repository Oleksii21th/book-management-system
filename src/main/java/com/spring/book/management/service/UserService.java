package com.spring.book.management.service;

import com.spring.book.management.dto.UserRegistrationRequestDto;
import com.spring.book.management.dto.UserResponseDto;
import com.spring.book.management.model.User;

public interface UserService {
    UserResponseDto registerUser(UserRegistrationRequestDto request);

    User getCurrentUser();
}
