package com.example.forumapplication.controllers;


import com.example.forumapplication.exceptions.EntityDuplicateException;
import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.filters.enums.UserSortField;
import com.example.forumapplication.mappers.UserMapper;
import com.example.forumapplication.models.User;
import com.example.forumapplication.models.dtos.UserDto;
import com.example.forumapplication.models.dtos.UserFiltersDto;
import com.example.forumapplication.services.contracts.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        UserFiltersDto filtersDto = new UserFiltersDto();
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        Page<User> userPage = new PageImpl<>(Collections.emptyList());

        when(userService.findAll(any(), any(), any(), any())).thenReturn(userPage);

        Page<User> result = userController.findAll(filtersDto, 0, 10, UserSortField.ID, Sort.Direction.DESC);

        assertEquals(userPage, result);
        verify(userService).findAll(any(), any(), any(), eq(pageRequest));
    }

    @Test
    void testFindById() {
        User user = new User();
        when(userService.findUserById(1)).thenReturn(user);

        User result = userController.findById(1);

        assertEquals(user, result);
        verify(userService).findUserById(1);
    }

    @Test
    void testCreate() {
        UserDto userDto = new UserDto();
        User user = new User();
        when(mapper.fromDto(userDto)).thenReturn(user);
        doAnswer(invocation -> null).when(userService).createUser(user);

        User result = userController.create(userDto);

        assertEquals(user, result);
        verify(mapper).fromDto(userDto);
        verify(userService).createUser(user);
    }
    @Test
    void testUpdate() {
        UserDto userDto = new UserDto();
        User user = new User();
        when(mapper.fromDto(userDto, 1)).thenReturn(user);
        doAnswer(invocation -> null).when(userService).updateUser(user);

        User result = userController.update(1, userDto);

        assertEquals(user, result);
        verify(mapper).fromDto(userDto, 1);
        verify(userService).updateUser(user);
    }

    @Test
    void testDelete() {
        User user = new User();
        when(userService.findUserById(1)).thenReturn(user);
        doNothing().when(userService).deleteUser(1);

        userController.delete(1);

        verify(userService).findUserById(1);
        verify(userService).deleteUser(1);
    }

    @Test
    void testUpdatePhoto() {
        User user = new User();
        when(userService.findUserById(1)).thenReturn(user);
        doNothing().when(userService).uploadPhoto(user, "newPhoto");

        User result = userController.updatePhoto(1, "newPhoto");

        assertEquals(user, result);
        verify(userService).findUserById(1);
        verify(userService).uploadPhoto(user, "newPhoto");
    }
    @Test
    void testCreateDuplicateUser() {
        UserDto userDto = new UserDto();
        User user = new User();
        when(mapper.fromDto(userDto)).thenReturn(user);
        doThrow(new EntityDuplicateException("User", "username",user.getUsername())).when(userService).createUser(user);

        Assertions.assertThrows(EntityDuplicateException.class, () -> {
            userController.create(userDto);
        });

        verify(mapper).fromDto(userDto);
        verify(userService).createUser(user);
    }

    @Test
    void testUpdateNotFound() {
        UserDto userDto = new UserDto();
        User user = new User();
        when(mapper.fromDto(userDto, 1)).thenReturn(user);
        doThrow(new EntityNotFoundException("User",user.getId())).when(userService).updateUser(user);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            userController.update(1, userDto);
        });

        verify(mapper).fromDto(userDto, 1);
        verify(userService).updateUser(user);
    }

    @Test
    void testDeleteNotFound() {
        doThrow(new EntityNotFoundException("User",1)).when(userService).findUserById(1);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            userController.delete(1);
        });

        verify(userService).findUserById(1);
    }

    @Test
    void testUpdatePhotoNotFound() {
        doThrow(new EntityNotFoundException("User", 1)).when(userService).findUserById(1);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            userController.updatePhoto(1, "newPhoto");
        });

        verify(userService).findUserById(1);
    }
}