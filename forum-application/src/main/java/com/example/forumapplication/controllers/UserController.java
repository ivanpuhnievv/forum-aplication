package com.example.forumapplication.controllers;

import com.example.forumapplication.exceptions.EntityDuplicateException;
import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.filters.enums.UserSortField;
import com.example.forumapplication.mappers.UserMapper;
import com.example.forumapplication.models.User;
import com.example.forumapplication.models.dtos.UserDto;
import com.example.forumapplication.models.dtos.UserFiltersDto;
import com.example.forumapplication.services.contracts.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Operations for managing users")
public class UserController {

    private final UserService userService;

    private final UserMapper mapper;

    @Autowired
    public UserController(UserService userService, UserMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping
    public Page<User> findAll(
            @RequestBody(required = false) UserFiltersDto filterOptions,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int sizePerPage,
            @RequestParam(defaultValue = "ID") UserSortField sortField,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection) {

        Pageable pageable = PageRequest.of(page, sizePerPage, sortDirection, sortField.getDatabaseFieldName());
        return userService.findAll(filterOptions.getUsername(), filterOptions.getEmail(), filterOptions.getFirstName(),
                pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id", description = "Get user by id")
    public User findById(@PathVariable int id) {
        try {
            return userService.findUserById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Create user")
    public User create(@Valid @RequestBody UserDto userDto, String username) {
        try {
            User user = mapper.fromDto(userDto, username);
            userService.createUser(user);
            return user;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user")
    public User update(@PathVariable int id, @Valid @RequestBody UserDto userDto, String username) {
        try {
            User user = mapper.fromDto(userDto, id);
            userService.updateUser(user);
            return user;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete user")
    public void delete(@PathVariable int id, String username) {
        try {
            User user = userService.findUserById(id);
            userService.deleteUser(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


}