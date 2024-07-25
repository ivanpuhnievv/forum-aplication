package com.example.forumapplication.services.contracts;

import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;

import java.util.List;

public interface CommentService {

    Comment getCommentById(int id);

    List<Comment> getComments();

    Post addComment(int postId, Comment comment);

    Comment updateComment(int commentId, Comment updatedComment);

    void deleteComment(int commentId, User user);
}
