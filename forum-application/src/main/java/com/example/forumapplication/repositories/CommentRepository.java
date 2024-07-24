package com.example.forumapplication.repositories;

import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByCreatedBy(Comment createdBy);
//    List<Comment> findCommentByPost(Comment comment, Post post);
    Comment findCommentById(int id);

//    Comment addComment(Comment newComment);
//    List<Comment> findByPostId(Post post);

}
