package com.example.forumapplication.controllers;

import com.example.forumapplication.exceptions.AuthorizationException;
import com.example.forumapplication.exceptions.EntityDuplicateException;
import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.helpers.AuthenticationHelper;
import com.example.forumapplication.mappers.CommentMapper;
import com.example.forumapplication.mappers.PostMapper;
import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import com.example.forumapplication.models.dtos.CommentDto;
import com.example.forumapplication.models.dtos.PostDto;
import com.example.forumapplication.services.contracts.PostService;
import com.example.forumapplication.services.contracts.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Posts", description = "Operations for managing posts and add comments to posts")
public class PostController {

    private final PostService postService;
    private final AuthenticationHelper authenticationHelper;
    private final PostMapper mapper;
    private final CommentMapper commentMapper;
    private final UserService userService;

    @Autowired
    public PostController(PostService postService, PostMapper mapper,
                          AuthenticationHelper authenticationHelper, CommentMapper commentMapper,
                          UserService userService) {
        this.postService = postService;
        this.mapper = mapper;
        this.authenticationHelper = authenticationHelper;
        this.commentMapper = commentMapper;
        this.userService = userService;
    }

    // Get all posts
    @GetMapping
    public List<Post> findAll(){
        return postService.get();
    }

    // Get post by ID
    @GetMapping("/{id}")
    public Post get(@PathVariable int id) {
        try {
            return postService.getById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // Create a new post
    @PostMapping
    public Post create(@RequestHeader HttpHeaders headers, @Valid @RequestBody PostDto postDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = mapper.fromDto(postDto);
            postService.create(post, user);
            return post;
        }catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    // Update an existing post
    @PutMapping("/{id}")
    public Post update(@RequestHeader HttpHeaders headers,@PathVariable int id, @Valid @RequestBody PostDto postDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = mapper.fromDto(id, postDto);
            postService.update(post, user);
            return post;
        }catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    // Delete a post
    @DeleteMapping("/{id}")
    public void delete(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            postService.delete(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/users/{id}")
    public List<Post> getUserPosts(@PathVariable int id) {
        try {
            User user = userService.findUserById(id);
            return postService.getPostsByUser(user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
