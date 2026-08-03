package com.foodtable.express.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.security.InvalidParameterException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import com.foodtable.express.auth.model.Role;
import com.foodtable.express.auth.model.User;
import com.foodtable.express.auth.model.login.dto.LoginRequestDto;
import com.foodtable.express.auth.model.login.dto.LoginResponseDto;
import com.foodtable.express.auth.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginService loginService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(loginService, "jwtExpirationSeconds", 600L);
    }

    @Test
    public void login_ShouldReturnToken_WhenCredentialsAreValid() {
        LoginRequestDto request = new LoginRequestDto("jane@test.com", "password123");

        Role basicRole = new Role();
        basicRole.setRoleId(Role.RoleTypes.BASIC.getRoleId());
        basicRole.setName("basic");

        User user = new User("Jane Doe", "jane@test.com", "encodedPass", Set.of(basicRole));
        user.setUserId(UUID.randomUUID());

        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("mocked.jwt.token");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        LoginResponseDto response = loginService.login(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.token());
        assertEquals(600L, response.expiresAt());

        verify(userRepository).findByEmail(request.email());
        verify(passwordEncoder).matches(request.password(), user.getPassword());
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    public void login_ShouldThrowInvalidParameterException_WhenEmailNotFound() {
        LoginRequestDto request = new LoginRequestDto("jane@test.com", "password123");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThrows(InvalidParameterException.class, () -> loginService.login(request));

        verify(userRepository).findByEmail(request.email());
        verifyNoInteractions(passwordEncoder, jwtEncoder);
    }

    @Test
    public void login_ShouldThrowInvalidParameterException_WhenPasswordIncorrect() {
        LoginRequestDto request = new LoginRequestDto("jane@test.com", "password123");
        User user = new User("Jane Doe", "jane@test.com", "encodedPass", Set.of());
        user.setUserId(UUID.randomUUID());

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidParameterException.class, () -> loginService.login(request));

        verify(userRepository).findByEmail(request.email());
        verify(passwordEncoder).matches(request.password(), user.getPassword());
        verifyNoInteractions(jwtEncoder);
    }
}
