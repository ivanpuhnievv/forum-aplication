package com.example.forumapplication.controllers.mvc;

import com.example.forumapplication.models.User;
import com.example.forumapplication.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class BaseController {

    @Autowired
    private UserService userService;

    @ModelAttribute("loggedInUser")
    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return convertToUser(userDetails);
        }
        return null;
    }

    private User convertToUser(UserDetails userDetails) {
        // Създайте нов User и задайте необходимите полета на базата на UserDetails
        User user = new User();
        user = userService.findUserByUsername(userDetails.getUsername());

        // Добавете другите полета, които са необходими
        return user;
    }
}