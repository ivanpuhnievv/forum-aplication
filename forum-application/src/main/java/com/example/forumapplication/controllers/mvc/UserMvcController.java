package com.example.forumapplication.controllers.mvc;

import com.example.forumapplication.models.User;
import com.example.forumapplication.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class UserMvcController {

    private final UserService userService;

    @Autowired
    public UserMvcController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("users")
    public List<User> populateUsers() {
        return userService.getAll();
    }

    @GetMapping("/users")
    public String getUsers() {
        return "";  // Това трябва да е името на вашия Thymeleaf шаблон (users.html)
    }



}
