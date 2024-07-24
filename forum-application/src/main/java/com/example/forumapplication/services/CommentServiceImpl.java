package com.example.forumapplication.services;

import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import com.example.forumapplication.repositories.CommentRepository;
import com.example.forumapplication.repositories.PostRepository;
import com.example.forumapplication.services.contracts.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CommentServiceImpl implements CommentService {

    public static final String UNAUTHORIZED_OPERATION = "Unauthorized operation.";
    public static final String MULTIPLE_LIKE_ERROR = "You have already liked this comment";
    public static final String YOU_ARE_THE_CREATOR_OF_THIS_COMMENT = "You are the creator of this comment";

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Override
    public Comment getCommentById(int id) {
        return commentRepository.findCommentById(id);
    }

    @Override
    public List<Comment> getComments() {
        return commentRepository.findAll();
    }

    public Comment addComment(int postId,Comment comment) {
        Post post = postRepository.getById(postId);
        Comment newComment = comment;


        return newComment;
    }

    // Update an existing comment
    public Comment updateComment(int commentId, Comment updatedComment) {
        Comment existingComment = getCommentById(commentId);
        existingComment.setContent(updatedComment.getContent());
        return commentRepository.save(existingComment);
    }


    // Delete a comment by its ID
    public void deleteComment(int commentId) {
        Comment comment = getCommentById(commentId);
        commentRepository.delete(comment);
    }
//
//    @Override
//    public List<Comment> getCommentsByAuthor(User user) {
//        return List.of();
//    }
}
