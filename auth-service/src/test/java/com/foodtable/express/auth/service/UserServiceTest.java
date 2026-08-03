package com.foodtable.express.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.foodtable.express.auth.infra.exception.EmailAlreadyExistsException;
import com.foodtable.express.auth.model.Role;
import com.foodtable.express.auth.model.User;
import com.foodtable.express.auth.model.register.dto.RequestUserDto;
import com.foodtable.express.auth.model.register.dto.ResponseUserDto;
import com.foodtable.express.auth.repository.RoleRepository;
import com.foodtable.express.auth.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void register_ShouldCreateUser_WhenValidRequest() {
        RequestUserDto request = new RequestUserDto("John N", "John@test.com", "pass123");
        Role basicRole = new Role();
        basicRole.setRoleId(Role.RoleTypes.BASIC.getRoleId());
        basicRole.setName("basic");

        User savedUser = new User("John N", "John@test.com", "encodedPass", Set.of(basicRole));
        savedUser.setUserId(UUID.randomUUID());

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(roleRepository.findById(Role.RoleTypes.BASIC.getRoleId())).thenReturn(Optional.of(basicRole));
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        ResponseUserDto response = userService.register(request);

        assertNotNull(response);
        assertEquals(savedUser.getUserId().toString(), response.id());
        assertEquals(savedUser.getEmail(), response.email());

        verify(userRepository).findByEmail(request.email());
        verify(roleRepository).findById(Role.RoleTypes.BASIC.getRoleId());
        verify(passwordEncoder).encode(request.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void register_ShouldThrowEmailAlreadyExistsException_WhenEmailIsTaken() {
        RequestUserDto request = new RequestUserDto("John N", "John@test.com", "pass123");
        User existingUser = new User();

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existingUser));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.register(request));

        verify(userRepository).findByEmail(request.email());
        verifyNoInteractions(roleRepository, passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void register_ShouldThrowIllegalStateException_WhenBasicRoleNotFound() {
        RequestUserDto request = new RequestUserDto("John N", "John@test.com", "pass123");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(roleRepository.findById(Role.RoleTypes.BASIC.getRoleId())).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> userService.register(request));

        verify(userRepository).findByEmail(request.email());
        verify(roleRepository).findById(Role.RoleTypes.BASIC.getRoleId());
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }
}
