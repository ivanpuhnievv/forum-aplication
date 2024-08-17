package com.example.forumapplication.controllers.mvc;

import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.mappers.PostMapper;
import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import com.example.forumapplication.models.dtos.PostDto;
import com.example.forumapplication.services.contracts.CommentService;
import com.example.forumapplication.services.contracts.PostService;
import com.example.forumapplication.services.contracts.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/posts")
public class PostsController extends BaseController {

        private final PostService postService;
        private final UserService userService;
        private final CommentService commentService;
        private final PostMapper postMapper;

        @Autowired
        public PostsController(PostService postService, UserService userService, CommentService commentService, PostMapper postMapper) {
            this.postService = postService;
            this.userService = userService;
            this.commentService = commentService;
            this.postMapper = postMapper;
        }

    @GetMapping
    public String listPosts(Model model) {
        List<Post> posts = postService.getAll();// Пример за метод, който взима всички постове
        model.addAttribute("active", "posts");
        model.addAttribute("posts", posts);
        return "posts";  // Връщане на изглед, който визуализира списъка с постове
    }

    @GetMapping("/filter")
    public String filterPosts(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int sizePerPage,
            @RequestParam(defaultValue = "comments") String sort,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection,
            Model model) {

        // Определяне на полето за сортиране
        String sortField = sort.equals("likes") ? "likes" : "comments";

        // Настройка на Pageable за пагинация и сортиране
        Pageable pageable = PageRequest.of(page, sizePerPage, sortDirection, sortField);

        // Извикване на услугата за търсене и филтриране
        Page<Post> posts = postService.findAll(username, email, title, pageable);

        // Обработка на пагинация
        int totalPages = posts.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        // Добавяне на атрибути към модела
        model.addAttribute("posts", posts);
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("title", title);
        model.addAttribute("sort", sort);

        return "posts";  // Тук задайте името на вашия изглед
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
            commentService.addReply(id,reply,user);
            return "redirect:/posts";
        }


    @GetMapping("/create")
    public String showPostCreatePage(Model model) {
        model.addAttribute("post", new PostDto());
        return "create-post-page";
    }

       @PostMapping("/create")
       public String createPost(@Valid PostDto postDto, BindingResult result,
                                Model model,Principal principal) {
        if (result.hasErrors()) {
            return "create-post-page";
        }

        try {
            Post post = postMapper.fromDto(postDto);
            postService.create(post);

            return "redirect:/posts";
        }catch (EntityNotFoundException e){
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "create-post-page";
        }
    }
}