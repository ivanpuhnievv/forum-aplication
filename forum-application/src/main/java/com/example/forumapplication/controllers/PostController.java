package com.example.forumapplication.controllers;

import com.example.forumapplication.exceptions.AuthorizationException;
import com.example.forumapplication.exceptions.EntityDuplicateException;
import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.mappers.PostMapper;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import com.example.forumapplication.models.dtos.PostDto;
import com.example.forumapplication.models.dtos.TagDto;
import com.example.forumapplication.services.contracts.PostService;
import com.example.forumapplication.services.contracts.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Posts", description = "Operations for managing posts and add comments to posts")
public class PostController {

    private final PostService postService;
    private final PostMapper mapper;
    private final UserService userService;

    @Autowired
    public PostController(PostService postService, PostMapper mapper,
                          UserService userService)
    {
        this.postService = postService;
        this.mapper = mapper;
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
    public Post create(@Valid @RequestBody PostDto postDto) {
        try {
            Post post = mapper.fromDto(postDto);
            postService.create(post);
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
    public Post update(@PathVariable int id, @Valid @RequestBody PostDto postDto) {
        try {
            Post post = mapper.fromDto(id, postDto);
            postService.update(post);
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
    public void delete(@PathVariable int id) {
        try {
            postService.delete(id);
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

    @PutMapping("/{id}/like")
    public void likePost(@PathVariable int id) {
        try {
            postService.likePost(id);
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}/like")
    public void unlikePost(@PathVariable int id) {
        try {
            postService.removeLike(id);
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{postId}/tag/{tagId}")
    public void addTagToPost(@PathVariable int postId, @PathVariable int tagId) {
        try{
            postService.addTag(postId,tagId);
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (EntityDuplicateException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/{postId}/tag/{tagId}")
    public void deleteTagFromPost(@PathVariable int postId, @PathVariable int tagId) {
        try{
            postService.deleteTag(postId,tagId);
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (EntityDuplicateException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{postId}/tagMod/{tagId}")
    public void changeTagFromPost(@PathVariable int postId, @PathVariable int tagId, @Valid @RequestBody TagDto tagDto) {
        try{
            postService.changeTag(postId,tagId,tagDto);
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (EntityDuplicateException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

}
