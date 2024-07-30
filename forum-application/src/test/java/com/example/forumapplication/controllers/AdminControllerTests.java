package com.example.forumapplication.controllers;

import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.mappers.UserMapper;
import com.example.forumapplication.models.User;
import com.example.forumapplication.models.dtos.UserDto;
import com.example.forumapplication.services.contracts.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.example.forumapplication.helpers.TestHelpers.createAuthorityList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper mapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

    }

    @Test
    void testCreateAdmin() {
        UserDto userDto = new UserDto();
        User user = new User();
        String role = "ADMIN";

        when(mapper.fromDto(userDto)).thenReturn(user);
        doNothing().when(userService).createUserWithRole(user, role);

        User result = adminController.createAdmin(userDto, role);

        assertEquals(user, result);
        verify(authentication).getName();
        verify(mapper).fromDto(userDto);
        verify(userService).createUserWithRole(user, role);
    }

    @Test
    void testCreateAdminWithNonAdminRole() {
        UserDto userDto = new UserDto();
        User user = new User();
        String role = "USER";

        when(mapper.fromDto(userDto)).thenReturn(user);
        doNothing().when(userService).createUserWithRole(user, role);

        User result = adminController.createAdmin(userDto, role);

        assertNull(user.getPhone());
        assertEquals(user, result);
        verify(authentication).getName();
        verify(mapper).fromDto(userDto);
        verify(userService).createUserWithRole(user, role);
    }


}