//package com.example.forumapplication.controllers.mvc;
//
//import com.example.forumapplication.models.User;
//import com.example.forumapplication.services.contracts.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.ModelAttribute;
//
//import java.security.Principal;
//
//@Controller
//public class NavBarController {
//
//    private final UserService userService;
//
//    @Autowired
//    public NavBarController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @ModelAttribute("loggedInUser")
//    public User getLoggedInUser(Principal principal) {
//        if (principal != null) {
//            // Assuming you have a userService to get user details by username
//            return userService.findUserByUsername(principal.getName());
//        }
//        return null;
//    }
//}