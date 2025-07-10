package com.spring.book.management.mapper;

import com.spring.book.management.config.MapperConfig;
import com.spring.book.management.dto.user.UserRegistrationRequestDto;
import com.spring.book.management.dto.user.UserResponseDto;
import com.spring.book.management.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toEntity(UserRegistrationRequestDto request);
}
