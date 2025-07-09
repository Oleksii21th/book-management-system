package com.spring.book.management.security;

import com.spring.book.management.dto.user.UserLoginRequestDto;
import com.spring.book.management.dto.user.UserLoginResponseDto;
import com.spring.book.management.exception.LoginException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public UserLoginResponseDto authenticateUser(UserLoginRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));
            String token = jwtUtil.generateToken(authentication);
            return new UserLoginResponseDto(token);
        } catch (BadCredentialsException ex) {
            throw new LoginException("Nieprawidłowy login lub hasło");
        }
    }
}
