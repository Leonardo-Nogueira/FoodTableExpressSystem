package com.foodtable.express.auth.service;

import java.security.InvalidParameterException;
import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import com.foodtable.express.auth.model.login.dto.LoginRequestDto;
import com.foodtable.express.auth.model.login.dto.LoginResponseDto;
import com.foodtable.express.auth.repository.UserRepository;

@Service
public class LoginService {

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginService(JwtEncoder jwtEncoder,
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDto login(LoginRequestDto login) {

        var userEmail = login.email();
        var userPassword = login.password();

        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new InvalidParameterException("Email ou senha inválidos."));

        if (!passwordEncoder.matches(userPassword, user.getPassword())) {
            throw new InvalidParameterException("Email ou senha inválidos.");
        }

        var now = Instant.now();
        var expiresAt = 600L;

        var claims = JwtClaimsSet
                .builder()
                .issuer("Auth-Service")
                .subject(user.getUserId().toString())
                .expiresAt(now.plusSeconds(expiresAt))
                .build();

        var jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new LoginResponseDto(jwt, expiresAt);
    }

}
