package com.example.forumapplication.controllers.mvc;

import com.example.forumapplication.models.Post;
import com.example.forumapplication.services.contracts.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController extends BaseController{

    private final PostService postService;
    @Autowired
    public HomeController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String landing() {
        return "home";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication, Post post) {
        List<Post> posts = postService.getAll();
        model.addAttribute("posts", posts);
        model.addAttribute("post", post);
        return "home";
    }


}
