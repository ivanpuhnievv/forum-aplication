package com.example.forumapplication.controllers.mvc;

import com.example.forumapplication.models.User;
import com.example.forumapplication.models.dtos.UserDto;
import com.example.forumapplication.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;

@Controller
public class UserMvcController extends BaseController {

    private final UserService userService;


    @Autowired
    public UserMvcController(UserService userService) {
        this.userService = userService;

    }
    private static String UPLOAD_DIR = "forum-application/src/main/resources/static/images/";

    @GetMapping("/users")
    public String getUsers(Model model, UserDto userDto) {
        List<User> users = userService.getAll();
        model.addAttribute("users", users);
        model.addAttribute("user", userDto);
        return "users-page";
    }

    @GetMapping("/users/{id}")
    public String getUser(Model model, @ModelAttribute("id") int id) {
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        return "user-page";
    }

    @PostMapping("/users/photo")
    public String uploadProfilePhoto(@RequestParam("profilePhoto") MultipartFile file,
                                     @AuthenticationPrincipal UserDetails loggedInUser,
                                     RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/users/" + userService.findUserByUsername(loggedInUser.getUsername()).getId();
        }

        try {
            String oldPhoto = userService.findUserByUsername(loggedInUser.getUsername()).getProfilePhoto();
            // Създайте уникално име за файла
            String fileName = loggedInUser.getUsername() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // Проверете дали директорията съществува, ако не - я създайте
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Запазете файла в директорията
            Path path = uploadPath.resolve(fileName);
            Files.write(path, file.getBytes());

            // Запишете адреса на файла в базата данни
            userService.updateUserProfilePhoto(loggedInUser.getUsername(), fileName);

            if (oldPhoto != null) {
                Files.delete(Paths.get(UPLOAD_DIR + oldPhoto));
            }


            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + fileName + "'");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message",
                    "Failed to upload '" + file.getOriginalFilename() + "'");
        }

        return "redirect:/users/" + userService.findUserByUsername(loggedInUser.getUsername()).getId();
    }

    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam("userId") int userId,
                             @AuthenticationPrincipal UserDetails loggedInUser,
                             RedirectAttributes redirectAttributes) {
        User user = userService.findUserById(userId);
        User currentUser = userService.findUserByUsername(loggedInUser.getUsername());

        if (currentUser.getId() == user.getId() || currentUser.getRole_id().getName().equals("ADMIN")) {
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully.");
            return "redirect:/home";
        } else {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to delete this profile.");
            return "redirect:/users/" + userId;
        }
    }
}

