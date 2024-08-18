package com.example.forumapplication.repositories;

import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    Post getById(int id);

    List<Post> findByCreatedBy(User user);

    Page<Post> findAll(Specification<Post> filters, Pageable pageable);

    List<Post> findByCreatedBy_Id(int userId);

    @Query("SELECT p FROM Post p LEFT JOIN p.comments c GROUP BY p ORDER BY COUNT(c) DESC")
    Page<Post> findTop5ByOrderByCommentsDesc(Pageable pageable);

}

