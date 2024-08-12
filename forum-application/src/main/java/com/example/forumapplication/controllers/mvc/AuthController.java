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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "sign-in";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "sign-up";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserDto dto, BindingResult bindingResult, Model model) {
        if (!userMapper.checkPassword(dto.getPassword(), dto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password_error", "Passwords do not match");
            return "redirect:/auth/register?error=true";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", dto);
//            return bindingResult.getAllErrors().toString();
            return "redirect:/auth/register";
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
