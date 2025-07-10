package com.spring.book.management.service;

import com.spring.book.management.dto.user.UserRegistrationRequestDto;
import com.spring.book.management.dto.user.UserResponseDto;
import com.spring.book.management.model.User;

public interface UserService {
    UserResponseDto registerUser(UserRegistrationRequestDto request);

    User getCurrentUser();
}
