package com.student.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // ✅ Disable CSRF
            .csrf(csrf -> csrf.disable())

            // ✅ Allow ALL required endpoints
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/login",
                        "/signup",
                        "/dashboard",
                        "/style.css",
                        "/script.js",
                        "/auth/**",
                        "/students/**"   // 🔥 THIS IS MISSING IN YOUR CODE
                ).permitAll()

                .anyRequest().permitAll() // 🔥 allow everything for now
            )

            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}