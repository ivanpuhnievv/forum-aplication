package com.example.forumapplication.services.contracts;

import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;

import java.util.List;

public interface CommentService {

    Comment getCommentById(int id);

    Post addComment(int postId, Comment comment);

    Comment updateComment(int commentId, Comment updatedComment);

    void deleteComment(int commentId);

    Comment addReply(int id, Comment comment,User currentUser);
    List<Comment> findUnreadCommentsByUserId(int userId);
    void markCommentsAsRead(User user);
    public void markCommentAsRead(int commentId);
}
