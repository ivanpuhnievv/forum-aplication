package com.example.forumapplication.controllers.mvc;

import com.example.forumapplication.exceptions.EntityDuplicateException;
import com.example.forumapplication.mappers.UserMapper;
import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import com.example.forumapplication.models.dtos.PostDto;
import com.example.forumapplication.models.dtos.UserDto;
import com.example.forumapplication.services.contracts.CommentService;
import com.example.forumapplication.services.contracts.PostService;
import com.example.forumapplication.services.contracts.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostsController extends BaseController {

    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public PostsController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping
    public String showPosts(Model model) {
        List<Post> posts = postService.getAll();
        model.addAttribute("posts", posts);
        return "posts"; // Уверете се, че имате Thymeleaf шаблон с име "posts.html"
    }

    // Контролер за добавяне на нов коментар към пост
    @PostMapping("/posts/{id}/comments/add")
    public String addComment(@PathVariable("id") int postId,
                             @RequestParam("comment") String commentContent,
                             Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Comment comment = new Comment();
            comment.setContent(commentContent);
//            comment.setCreatedBy(username);
            commentService.addComment(postId, comment);
        }
        return "redirect:/posts"; // Пренасочване към страницата с постовете
    }

    @PostMapping("/{id}/like")
    public String likePost(@PathVariable("id") int postId, Principal principal,Model model) {
        String user = principal.getName();
        model.addAttribute("user", user);
        // Add the logic to handle the like functionality
        postService.likePost(postId);
        return "redirect:/posts"; // Redirect to the posts page or wherever you want
    }
}

