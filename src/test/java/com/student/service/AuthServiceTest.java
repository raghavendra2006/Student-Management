package com.student.service;

import com.student.model.User;
import com.student.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    //  Test Register
    @Test
    void testRegister() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("1234");

        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = authService.register(user);

        assertNotNull(savedUser);
        assertEquals("encoded1234", savedUser.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    //  Test Login Success
    @Test
    void testLoginSuccess() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("encoded1234");

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("1234", "encoded1234")).thenReturn(true);

        User result = authService.login("test", "1234");

        assertNotNull(result);
        assertEquals("test", result.getUsername());
    }

    // Test Login - User Not Found
    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                authService.login("test", "1234"));

        assertEquals("User not found", ex.getMessage());
    }

    // Test Login - Wrong Password
    @Test
    void testLoginInvalidPassword() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("encoded1234");

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded1234")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                authService.login("test", "wrong"));

        assertEquals("Invalid password", ex.getMessage());
    }
}