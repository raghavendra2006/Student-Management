package com.student.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.config.JwtUtil;
import com.student.model.User;
import com.student.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ Register Test
    @Test
    void testRegister() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword("1234");

        when(authService.register(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"));
    }

    // ✅ Login Test
    @Test
    void testLogin() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword("1234");

        when(authService.login("test", "1234")).thenReturn(user);
        when(jwtUtil.generateToken("test")).thenReturn("mock-token");

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("mock-token"));
    }
}