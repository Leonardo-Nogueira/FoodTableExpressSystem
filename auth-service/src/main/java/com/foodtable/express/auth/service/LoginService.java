package com.foodtable.express.auth.service;

import java.security.InvalidParameterException;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.util.List;
import com.foodtable.express.auth.model.Role;
import com.foodtable.express.auth.model.login.dto.LoginRequestDto;
import com.foodtable.express.auth.model.login.dto.LoginResponseDto;
import com.foodtable.express.auth.repository.UserRepository;

@Service
public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${jwt.expiration-seconds:600}")
    private long jwtExpirationSeconds;

    public LoginService(JwtEncoder jwtEncoder,
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDto login(LoginRequestDto login) {
        var userEmail = login.email();
        log.info("Login attempt for email: {}", userEmail);

        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.warn("Login failed: email {} not found", userEmail);
                    return new InvalidParameterException("Invalid email or password.");
                });

        if (!passwordEncoder.matches(login.password(), user.getPassword())) {
            log.warn("Login failed: incorrect password for email {}", userEmail);
            throw new InvalidParameterException("Invalid email or password.");
        }

        var now = Instant.now();
        log.info("Login successful for email: {}. Generating JWT token...", userEmail);

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        var claims = JwtClaimsSet
                .builder()
                .issuer("Auth-Service")
                .subject(user.getUserId().toString())
                .claim("roles", roles)
                .expiresAt(now.plusSeconds(jwtExpirationSeconds))
                .build();

        var jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new LoginResponseDto(jwt, jwtExpirationSeconds);
    }

}
