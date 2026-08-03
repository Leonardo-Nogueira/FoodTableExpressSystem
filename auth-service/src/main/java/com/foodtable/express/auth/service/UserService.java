package com.foodtable.express.auth.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.foodtable.express.auth.infra.exception.EmailAlreadyExistsException;
import com.foodtable.express.auth.model.Role;
import com.foodtable.express.auth.model.User;
import com.foodtable.express.auth.model.register.dto.RequestUserDto;
import com.foodtable.express.auth.model.register.dto.ResponseUserDto;
import com.foodtable.express.auth.repository.RoleRepository;
import com.foodtable.express.auth.repository.UserRepository;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
            RoleRepository roleRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseUserDto register(RequestUserDto request) {
        log.info("Attempting to register a new user with email: {}", request.email());

        if (userRepository.findByEmail(request.email()).isPresent()) {
            log.warn("Registration failed: email {} is already registered", request.email());
            throw new EmailAlreadyExistsException("Email already registered in the system.");
        }

        Role basicRole = roleRepository.findById(Role.RoleTypes.BASIC.getRoleId())
                .orElseThrow(() -> {
                    log.error("Critical error: BASIC role not found in the database during registration.");
                    return new IllegalStateException("BASIC Role not configured in the database.");
                });

        var email = request.email();
        var name = request.name();
        var password = passwordEncoder.encode(request.password());

        var role = Set.of(basicRole);

        var user = User.createUser(name, email, password, role);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getUserId());

        return ResponseUserDto.builder()
                .id(savedUser.getUserId().toString())
                .email(savedUser.getEmail())
                .build();
    }
}
