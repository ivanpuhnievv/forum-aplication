package com.example.forumapplication.controllers.mvc;


import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import com.example.forumapplication.services.contracts.CommentService;
import com.example.forumapplication.services.contracts.PostService;
import com.example.forumapplication.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/comments")
public class CommentsController {

    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;

    @Autowired
    public CommentsController(CommentService commentService, PostService postService, UserService userService) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
    }

    // Add a new comment to a post
    @PostMapping("/{postId}/add")
    public String addComment(@PathVariable("postId") int postId,
                             @RequestParam("commentContent") String commentContent,
                             Principal principal) {
        if (principal != null) {
            User user = userService.findUserByUsername(principal.getName());
            Post post = postService.getById(postId);

            Comment comment = new Comment();
            comment.setContent(commentContent);
            comment.setCreatedBy(user);
//            comment.setPost(post);

            commentService.addComment(post.getId(), comment);
        }
        return "redirect:/posts"; // Redirect back to the posts page
    }

    // Reply to an existing comment
    @PostMapping("/{commentId}/reply")
    public String replyToComment(@PathVariable("commentId") int commentId,
                                 @RequestParam("replyContent") String replyContent,
                                 Principal principal) {
        if (principal != null) {
            User user = userService.findUserByUsername(principal.getName());
            Comment parentComment = commentService.getCommentById(commentId);
//            Post post = commentService.; // Assuming that replies are linked to the same post as the original comment

            Comment replyComment = new Comment();
            replyComment.setContent(replyContent);
            replyComment.setCreatedBy(user);
//            replyComment.setPost(post);
            replyComment.setParentComment(parentComment); // Set the parent comment to establish the reply relationship

            commentService.addReply(commentId, replyComment,user);
        }
        return "redirect:/posts"; // Redirect back to the posts page
    }
}