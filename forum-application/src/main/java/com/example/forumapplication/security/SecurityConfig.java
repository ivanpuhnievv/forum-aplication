package com.example.forumapplication.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    public static final int BCRYPT_STRENGTH = 11;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorizeRequests ->
                                authorizeRequests
                                        .requestMatchers("/myCards").authenticated()
                                        .requestMatchers(HttpMethod.GET, "/api/users").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/users/").authenticated()
                                        .requestMatchers(HttpMethod.DELETE, "/api/users").authenticated()
                                        .requestMatchers(HttpMethod.POST, "api/users/register").permitAll()
                                        .requestMatchers(HttpMethod.POST, "api/users/admin/register").hasRole("ADMIN")
                                        .requestMatchers(HttpMethod.POST, "api/users/admin/register/").hasRole("ADMIN")
                                        .requestMatchers("/").permitAll()
                                        .requestMatchers("/contact").permitAll()
                                        .anyRequest().authenticated()
                )
                .csrf(cr -> cr.disable())
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }



}