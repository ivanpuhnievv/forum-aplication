package com.example.forumapplication.controllers.mvc;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping
    public String landing() {
        return "home";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        return "home";
    }
}
