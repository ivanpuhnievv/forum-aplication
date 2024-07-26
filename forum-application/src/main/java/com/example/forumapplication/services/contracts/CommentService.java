package com.example.forumapplication.services.contracts;

import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.Post;

public interface CommentService {

    Comment getCommentById(int id);

    Post addComment(int postId, Comment comment);

    Comment updateComment(int commentId, Comment updatedComment);

    void deleteComment(int commentId);

    Comment addReply(int id, Comment comment);
}
