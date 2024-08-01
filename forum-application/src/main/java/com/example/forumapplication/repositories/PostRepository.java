package com.example.forumapplication.repositories;

import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    Post getById(int id);

    List<Post> findByCreatedBy(User user);
}
