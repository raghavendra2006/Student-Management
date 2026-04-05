package com.student.controller;

import com.student.config.JwtUtil;
import com.student.model.User;
import com.student.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        User existingUser = authService.login(user.getUsername(), user.getPassword());
        return jwtUtil.generateToken(existingUser.getUsername());
    }
}