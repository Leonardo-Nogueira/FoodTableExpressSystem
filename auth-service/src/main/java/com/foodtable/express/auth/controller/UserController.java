package com.foodtable.express.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodtable.express.auth.model.login.dto.LoginRequestDto;
import com.foodtable.express.auth.model.register.dto.RequestUserDto;
import com.foodtable.express.auth.model.register.dto.ResponseUserDto;
import com.foodtable.express.auth.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ResponseUserDto> register(@Valid @RequestBody RequestUserDto dto) {
        return ResponseEntity.ok(userService.register(dto));
    }

    @PostMapping
    public ResponseEntity<LoginRequestDto> login(@Valid @RequestBody LoginRequestDto login) {
        return ResponseEntity.ok(userService.login(login));
    }

}
