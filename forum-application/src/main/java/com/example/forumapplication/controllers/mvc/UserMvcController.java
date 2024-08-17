package com.example.forumapplication.controllers.mvc;

import com.example.forumapplication.config.MyUserPrincipal;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import com.example.forumapplication.models.dtos.UserDto;
import com.example.forumapplication.services.contracts.CommentService;
import com.example.forumapplication.services.contracts.PostService;
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
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserMvcController extends BaseController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;


    @Autowired
    public UserMvcController(UserService userService, PostService postService
            , CommentService commentService) {
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;

    }

    private final static String UPLOAD_DIR = "forum-application/src/main/resources/static/images/";

    @GetMapping("/users")
    public String getUsers(Model model, UserDto userDto) {
        List<User> users = userService.getAll();
        model.addAttribute("active", "users");
        model.addAttribute("users", users);
        model.addAttribute("user", userDto);
        return "users-page";
    }

    @GetMapping("/users/{id}")
    public String getUser(Model model, @ModelAttribute("id") int id) {
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        List<Post> userPosts = postService.getPostsByUser(user);
        model.addAttribute("userPostsSize", userPosts.size());
        return "user-page";
    }

    @GetMapping("/user/posts")
    public String getUserPosts(Model model,Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        commentService.markCommentAsRead(user);
        List<Post> posts = postService.findByCreatedBy_Id(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        return "posts";
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
            // Създаваме уникално име за файла
            String fileName = loggedInUser.getUsername() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // Проверете дали директорията съществува, ако не - я създайте
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Запазваме файла в директорията
            Path path = uploadPath.resolve(fileName);
            Files.write(path, file.getBytes());

            // Записваме адреса на файла в базата данни
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
    @PostMapping("/setPhone")
    public String setPhone(@RequestParam("phone") String phone, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        user.setPhone(phone);
        userService.updateUser(user); // Запазване на промените
        return "redirect:/users/" + user.getId(); // Пренасочване към профила след успешно записване
    }

    @PostMapping("/users/setAdmin")
    public String setAdmin(@RequestParam("userId") int userId, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        if (user.getRole_id().getName().equals("ADMIN")) {
            User userToPromote = userService.findUserById(userId);
            userService.changeRole(userToPromote, userService.getRoleByName("ADMIN"));
        }
        return "redirect:/users/" + userId;
    }

    @PostMapping("/users/setBan")
    public String setBan(@RequestParam("userId") int userId, Principal principal,Model model) {
        User user = userService.findUserByUsername(principal.getName());
        model.addAttribute("user", user);
        if (user.getRole_id().getName().equals("ADMIN")) {
            User userToBan = userService.findUserById(userId);
            userService.banUser(userToBan);
        }
        return "redirect:/users/" + userId;
    }

    @PostMapping("/users/setUnban")
    public String setUnban(@RequestParam("userId") int userId, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        if (user.getRole_id().getName().equals("ADMIN")) {
            User userToUnban = userService.findUserById(userId);
            userService.unbanUser(userToUnban);
        }
        return "redirect:/users/" + userId;
    }

    @PostMapping("/users/setBlock")
    public String setBlock(@RequestParam("userId") int userId, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        if (user.getRole_id().getName().equals("MODERATOR")) {
            User userToBlock = userService.findUserById(userId);
            userService.blockUser(userToBlock);
        }
        return "redirect:/users/" + userId;
    }
    @PostMapping("/users/setUnblock")
    public String setUnblock(@RequestParam("userId") int userId, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        if (user.getRole_id().getName().equals("MODERATOR")) {
            User userToUnblock = userService.findUserById(userId);
            userService.unblockUser(userToUnblock);
        }
        return "redirect:/users/" + userId;
    }
}

