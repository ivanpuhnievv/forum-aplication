package com.example.forumapplication.repositories;

import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Comment findCommentById(int id);

//    @Query("SELECT c FROM Comment c WHERE c.createdBy = :user AND c.isRead = false")
//    List<Comment> findUnreadCommentsByUserId(@Param("user") int userId);

    @Query("SELECT c FROM Comment c WHERE c.createdBy = :user AND c.isRead = false")
    List<Comment> findUnreadCommentsByUser(@Param("user") User user);


}
