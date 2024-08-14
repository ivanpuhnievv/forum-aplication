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
import org.springframework.data.domain.Sort;
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
        private final UserService userService;
        private final CommentService commentService;

        @Autowired
        public PostsController(PostService postService, UserService userService, CommentService commentService) {
            this.postService = postService;
            this.userService = userService;
            this.commentService = commentService;
        }

    @GetMapping
    public String listPosts(Model model) {
        List<Post> posts = postService.getAll();  // Пример за метод, който взима всички постове
        model.addAttribute("posts", posts);
        return "posts";  // Връщане на изглед, който визуализира списъка с постове
    }

        @GetMapping("/filter")
        public String filterPosts(
                @RequestParam(required = false) String username,
                @RequestParam(required = false) String email,
                @RequestParam(required = false) String title,
                @RequestParam(required = false) String sort,
                Model model) {
            List<Post> posts = postService.filterAndSortPosts(username, email, title, sort);
            model.addAttribute("posts", posts);
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("title", title);
            model.addAttribute("sort", sort);
            return "posts/list";
        }

        // Контролер за харесванията
        @PostMapping("/{id}/like")
        public String likePost(@PathVariable int id, Principal principal) {
            User user = userService.findUserByUsername(principal.getName());
            postService.likePost(id);
            return "redirect:/posts";
        }

        // Контролер за добавяне на коментар към пост
        @PostMapping("/{id}/comments/add")
        public String addComment(@PathVariable int id, @RequestParam String comment, Principal principal) {
            User user = userService.findUserByUsername(principal.getName());
            Comment newComment = new Comment();
            newComment.setContent(comment);
            newComment.setCreatedBy(user);
//            postService.addCommentToPost(id, comment, user);
            commentService.addComment(id,newComment);
            return "redirect:/posts";
        }

        // Контролер за отговор на коментар
        @PostMapping("/comments/{id}/reply")
        public String replyToComment(@PathVariable int id, @RequestParam String replyContent,Principal principal) {
            User user = userService.findUserByUsername(principal.getName());
            Comment reply = new Comment();
            reply.setContent(replyContent);
            reply.setCreatedBy(user);
            commentService.addReply(id,reply);
            return "redirect:/posts";
        }

    }