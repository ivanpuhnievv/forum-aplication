package com.example.forumapplication.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {


    @GetMapping("/login")
    public String login() {
        return "index";
    }


}
