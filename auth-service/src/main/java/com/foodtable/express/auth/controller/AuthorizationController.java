package com.foodtable.express.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodtable.express.auth.model.login.dto.LoginRequestDto;
import com.foodtable.express.auth.model.login.dto.LoginResponseDto;
import com.foodtable.express.auth.service.LoginService;

@RestController
@RequestMapping("/token")
public class AuthorizationController {

    private LoginService service;

    public AuthorizationController(LoginService service) {
        this.service = service;
    }

    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto login) {
        return ResponseEntity.ok(service.login(login));
    }
}
