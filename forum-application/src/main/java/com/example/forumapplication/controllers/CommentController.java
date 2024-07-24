package com.example.forumapplication.controllers;

import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.helpers.AuthenticationHelper;
import com.example.forumapplication.models.Comment;
import com.example.forumapplication.services.contracts.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comments", description = "Operations for managing comments")
public class CommentController {

    private final CommentService commentService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public CommentController(CommentService commentService, AuthenticationHelper authenticationHelper) {
        this.commentService = commentService;
        this.authenticationHelper = authenticationHelper;
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

    @PutMapping("/{id}")
    public Comment updateComment(@PathVariable int id, @RequestBody Comment updatedComment) {
        try {
            return commentService.updateComment(id, updatedComment);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


}