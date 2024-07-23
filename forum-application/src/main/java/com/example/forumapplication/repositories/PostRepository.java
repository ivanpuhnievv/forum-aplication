package com.example.forumapplication.repositories;

import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {

    Page<Post> findAll(Specification<User> filters, Pageable pageable);

    Post getById(int id);

    Post getByTitle(String title);

    void create(Post post);

    void update(Post post);

    void delete(int id);

}
