package com.example.forumapplication.controllers.mvc;

import com.example.forumapplication.models.User;
import com.example.forumapplication.security.CustomOAuth2User;
import com.example.forumapplication.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
            if (authentication.getPrincipal() instanceof UserDetails) {
                return convertToUser((UserDetails) authentication.getPrincipal());
            } else if (authentication.getPrincipal() instanceof OAuth2User) {
               ((OAuth2User) authentication.getPrincipal()).getName();
                return convertToUser(((OAuth2User) authentication.getPrincipal()).getAttribute("email").toString());
            }
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

    private User convertToUser(String email) {
        // Създайте нов User и задайте необходимите полета на базата на UserDetails
        User user = new User();
        user = userService.findUserByEmail(email);

        // Добавете другите полета, които са необходими
        return user;
    }

}