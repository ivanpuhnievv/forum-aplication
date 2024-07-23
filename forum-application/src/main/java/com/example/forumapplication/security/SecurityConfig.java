package com.example.forumapplication.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    public static final int BCRYPT_STRENGTH = 11;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(
                                "/api/test/**",
                                "/swagger-ui/**",
                                "/v2/api-docs",
                                "/javainuse-openapi/**",
                                "/javainuse-openapi",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/V3/api-docs/",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "configuration/ui",
                                "configuration/security",
                                "/api/users/**", // Обновено: правилният път
                                "/api/users"
                        )
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(cr -> cr.disable())
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
            }
        };

    }

}