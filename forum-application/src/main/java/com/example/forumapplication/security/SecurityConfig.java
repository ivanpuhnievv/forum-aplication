package com.example.forumapplication.security;

import com.example.forumapplication.services.CustomOAuth2UserService;
import com.example.forumapplication.services.contracts.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    public static final int BCRYPT_STRENGTH = 11;
    private final CustomOAuth2UserService oauthUserService;
    private final UserService userService;

    public SecurityConfig(CustomOAuth2UserService oauthUserService, @Lazy UserService userService) {
        this.oauthUserService = oauthUserService;
        this.userService = userService;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorizeRequests ->
                                authorizeRequests
                                        .requestMatchers("/myCards").authenticated()
                                        .requestMatchers("/auth/login").permitAll()
                                        .requestMatchers("/home").permitAll()
                                        .requestMatchers("/posts").permitAll()
                                        .requestMatchers("/").permitAll()
                                        .requestMatchers("/users/**").permitAll()
                                        .requestMatchers("/users").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/users").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/users/").authenticated()
                                        .requestMatchers(HttpMethod.DELETE, "/api/users").authenticated()
                                        .requestMatchers(HttpMethod.POST, "api/users/register").permitAll()
                                        .requestMatchers(HttpMethod.POST, "api/users/admin/register").hasRole("ADMIN")
                                        .requestMatchers(HttpMethod.POST, "api/users/admin/register/").hasRole("ADMIN")
                                        .requestMatchers("/").permitAll()
                                        .requestMatchers("/contact").permitAll()

//                                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/auth/login")
                        .defaultSuccessUrl("/home")
                        .failureUrl("/auth/login?error=true")
                        .permitAll())
                .oauth2Login(oauth -> oauth
                        .loginPage("/auth/login")
                        .userInfoEndpoint(info -> info.userService(oauthUserService))
                        .successHandler(new AuthenticationSuccessHandler() {
                            @Override
                            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
                                userService.processOAuthPostLogin(oauthUser.getEmail(), oauthUser.getAttribute("name"));
                                response.sendRedirect("/home");
                            }
                        }))
                .logout(logout ->
                        logout
                                .logoutUrl("/auth/logout") // URL за логаут
                                .invalidateHttpSession(true) // Изтриване на сесията
                                .clearAuthentication(true) // Изчистване на аутентикацията
                                .deleteCookies("JSESSIONID") // Изтриване на бисквитките, свързани със сесията
                                .logoutSuccessUrl("/home") // Пренасочване към /home след успешен логаут
                                .permitAll()
                )
                //.csrf().and()
//                .authorizeRequests()
//                .requestMatchers("/**").permitAll()
//                .anyRequest().authenticated();
                .csrf(AbstractHttpConfigurer::disable);
//                .csrf(cr -> cr.disable());
//                .formLogin(withDefaults());
//                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }



}