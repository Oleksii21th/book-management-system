package com.spring.book.management.service.impl;

import com.spring.book.management.dto.UserRegistrationRequestDto;
import com.spring.book.management.dto.UserResponseDto;
import com.spring.book.management.exception.RegistrationException;
import com.spring.book.management.mapper.UserMapper;
import com.spring.book.management.model.Role;
import com.spring.book.management.model.User;
import com.spring.book.management.repository.role.RoleRepository;
import com.spring.book.management.repository.user.UserRepository;
import com.spring.book.management.service.UserService;
import java.util.Collections;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDto registerUser(UserRegistrationRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new RegistrationException("Email is already in use.");
        }
        Role userRole = roleRepository.findByName(Role.RoleName.USER)
                .orElseThrow(() -> new RegistrationException("Role 'USER' not found"));

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(userRole));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
