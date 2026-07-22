package com.foodtable.express.auth.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.foodtable.express.auth.model.User;
import com.foodtable.express.auth.config.JwtGenerate;
import com.foodtable.express.auth.infra.exception.EmailAlreadyExistsException;
import com.foodtable.express.auth.model.login.dto.LoginRequestDto;
import com.foodtable.express.auth.model.login.dto.LoginResponseDto;
import com.foodtable.express.auth.model.mapper.UserMapper;
import com.foodtable.express.auth.model.register.dto.RequestUserDto;
import com.foodtable.express.auth.model.register.dto.ResponseUserDto;
import com.foodtable.express.auth.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerate jwtGenerate;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            JwtGenerate jwtGenerate,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtGenerate = jwtGenerate;
        this.authenticationManager = authenticationManager;
    }

    public ResponseUserDto register(RequestUserDto dto) {
        validateIfEmailAlreadyExists(dto.email());

        var encryptedPassword = passwordEncoder.encode(dto.password());
        var user = userMapper.toEntity(dto, encryptedPassword);
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        return userMapper.toResponse(user);
    }

    public LoginResponseDto login(LoginRequestDto loginDto) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var token = jwtGenerate.generateToken((User) auth.getPrincipal());
        return new LoginResponseDto(token);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    private void validateIfEmailAlreadyExists(String email) {
        var user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
    }

}
