package com.foodtable.express.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foodtable.express.auth.model.login.dto.LoginRequestDto;
import com.foodtable.express.auth.model.login.dto.LoginResponseDto;
import com.foodtable.express.auth.model.register.dto.RequestUserDto;
import com.foodtable.express.auth.model.register.dto.ResponseUserDto;
import com.foodtable.express.auth.service.LoginService;
import com.foodtable.express.auth.service.UserService;

import jakarta.validation.Valid;

@RestController
public class AuthorizationController {

    private final LoginService loginService;
    private final UserService userService;

    public AuthorizationController(LoginService loginService, UserService userService) {
        this.loginService = loginService;
        this.userService = userService;
    }

    @PostMapping("/token")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto login) {
        return ResponseEntity.ok(loginService.login(login));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseUserDto> register(@Valid @RequestBody RequestUserDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }
}
