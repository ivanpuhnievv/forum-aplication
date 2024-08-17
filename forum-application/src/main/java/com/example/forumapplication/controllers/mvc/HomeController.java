package com.example.forumapplication.controllers.mvc;

import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import com.example.forumapplication.services.contracts.CommentService;
import com.example.forumapplication.services.contracts.PostService;
import com.example.forumapplication.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController extends BaseController{

    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;
    @Autowired
    public HomeController(PostService postService, CommentService commentService
            , UserService userService) {
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
    }


    @GetMapping
    public String landing() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model, Principal principal, Post post) {
        List<Post> posts = postService.getAll();
        if (principal != null) {
            User user = userService.findUserByUsername(principal.getName());
            List<Comment> unreadComments = commentService.findUnreadCommentsByUserId(user.getId());
            model.addAttribute("unreadCommentsCount", unreadComments.size());
        }
        model.addAttribute("active", "home");
        model.addAttribute("posts", posts);
        model.addAttribute("post", post);
        return "home";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("active", "contact");
        return "contact";
    }
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("active", "about");
        return "about";
    }
}
