package com.example.forumapplication.controllers;

import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.mappers.UserMapper;
import com.example.forumapplication.models.User;
import com.example.forumapplication.models.dtos.UserDto;
import com.example.forumapplication.services.contracts.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper mapper;

    @PostMapping("/admin/register")
    @Operation(summary = "Create user with a Role", description = "Only Admin user can create user with a Role")
    public User createAdmin(@Valid @RequestBody UserDto userDto, @RequestParam String role) {

            Authentication auth = checkAuthentication();
            String nameOfUser = auth.getName();  // Получаване на потребителското име на текущия аутентикиран потребител
            User user = mapper.fromDto(userDto);
            if (!role.equals("ADMIN")) {
                user.setPhone(null);
            }
            userService.createUserWithRole(user, role);
            return new ResponseEntity<>(user, HttpStatus.CREATED).getBody();
    }

    @PutMapping("/admin/block/{id}")
    @Operation(summary = "Block user", description = "Only Admin user can block user")
    public User blockUser(@PathVariable int id) {
        checkAuthentication();
        User user = userService.findUserById(id);
        if (user == null) {
            throw new EntityNotFoundException("User" , id);
        }
        return userService.blockUser(user);
    }

    @PutMapping("/admin/unblock/{id}")
    @Operation(summary = "Unblock user", description = "Only Admin user can unblock user")
    public User unblockUser(@PathVariable int id) {
        checkAuthentication();
        User user = userService.findUserById(id);
        if (user == null) {
            throw new EntityNotFoundException("User" , id);
        }
        return userService.unblockUser(user);
    }

    private Authentication checkAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        return auth;
    }

}
