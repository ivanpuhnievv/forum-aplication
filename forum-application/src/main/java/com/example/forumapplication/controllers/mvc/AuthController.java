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
public class AuthController extends BaseController {

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
            model.addAttribute("errorMessage", "Passwords do not match");
            return "sign-up";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", dto);

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
            model.addAttribute("errorMessage", e.getMessage());
            return "sign-up";
        }

    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/home";
    }

}
