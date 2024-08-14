package com.example.forumapplication.repositories;

import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    Post getById(int id);

    List<Post> findByCreatedBy(User user);

    @Query("SELECT p FROM Post p WHERE " +
            "(:username IS NULL OR p.createdBy.username = :username) AND " +
            "(:email IS NULL OR p.createdBy.email = :email) AND " +
            "(:title IS NULL OR p.title LIKE %:title%) ")
    List<Post> findFilteredAndSortedByLikes(@Param("username") String username,
                                            @Param("email") String email,
                                            @Param("title") String title);

    @Query("SELECT p FROM Post p WHERE " +
            "(:username IS NULL OR p.createdBy.username = :username) AND " +
            "(:email IS NULL OR p.createdBy.email = :email) AND " +
            "(:title IS NULL OR p.title LIKE %:title%) " +
            "ORDER BY SIZE(p.comments) DESC")
    List<Post> findFilteredAndSortedByComments(@Param("username") String username,
                                               @Param("email") String email,
                                               @Param("title") String title);
}
