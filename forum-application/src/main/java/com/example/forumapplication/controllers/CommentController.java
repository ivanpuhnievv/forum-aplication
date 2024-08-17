package com.example.forumapplication.controllers;

import com.example.forumapplication.exceptions.AuthorizationException;
import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.mappers.CommentMapper;
import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import com.example.forumapplication.models.dtos.CommentDto;
import com.example.forumapplication.services.contracts.CommentService;
import com.example.forumapplication.services.contracts.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comments", description = "Operations for managing comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final UserService userService;

    @Autowired
    public CommentController(CommentService commentService, CommentMapper commentMapper,
                             UserService userService) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
        this.userService = userService;
    }

    @GetMapping("/posts/{postId}")
    public Comment getCommentsForPost(@PathVariable int postId) {
        try {
            return commentService.getCommentById(postId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Comment getCommentById(@PathVariable int id) {
        try {
            return commentService.getCommentById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/posts/{id}")
    public Post createComment(@PathVariable int id, @Valid @RequestBody CommentDto commentDto) {
        try {
            Comment comment = commentMapper.fromDto(commentDto);
            return commentService.addComment(id,comment);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Comment updateComment(@PathVariable int id, @RequestBody Comment updatedComment) {
        try {
            return commentService.updateComment(id, updatedComment);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable int id) {
        try {
            commentService.deleteComment(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/{id}")
    public Comment createReply(@PathVariable int id, @Valid @RequestBody CommentDto commentDto, Principal principal) {
        try {
            Comment comment = commentMapper.fromDto(commentDto);
            User user = userService.findUserByUsername(principal.getName());
            return commentService.addReply(id,comment,user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}