package com.example.forumapplication.controllers.mvc;

import com.example.forumapplication.exceptions.EntityDuplicateException;
import com.example.forumapplication.mappers.UserMapper;
import com.example.forumapplication.models.User;
import com.example.forumapplication.models.dtos.UserDto;
import com.example.forumapplication.services.contracts.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "login";  // същото име на HTML файла, защото и логин и регистрация са на същата страница
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserDto dto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", dto);
            return bindingResult.getAllErrors().toString();
        }

        try {
            User user = userMapper.fromDto(dto);
            userService.createUser(user);
            return "redirect:/auth/login";
        } catch (EntityDuplicateException e) {
            if (e.getMessage().contains("email")) {
                bindingResult.rejectValue("email", "email_error",e.getMessage());
            } else {
                bindingResult.rejectValue("username", "username_error", e.getMessage());
            }
            return "Registration successful for user: " + dto.getUsername();
        }

    }


}
