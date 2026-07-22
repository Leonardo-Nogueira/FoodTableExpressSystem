package com.foodtable.express.auth.model.mapper;

import org.springframework.stereotype.Component;

import com.foodtable.express.auth.model.User;
import com.foodtable.express.auth.model.register.dto.RequestUserDto;
import com.foodtable.express.auth.model.register.dto.ResponseUserDto;

@Component
public class UserMapper {

    public User toEntity(RequestUserDto request, String hashedPassword) {
        return User.builder()
                .email(request.email())
                .password(hashedPassword)
                .build();
    }

    public ResponseUserDto toResponse(User user) {
        return ResponseUserDto.builder()
                .email(user.getEmail())
                .id(user.getId() != null ? user.getId().toString() : null)
                .build();
    }

}