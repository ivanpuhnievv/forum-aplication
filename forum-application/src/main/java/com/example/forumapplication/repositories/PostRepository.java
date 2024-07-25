package com.example.forumapplication.repositories;

import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    Optional<Post> findByTitle(String title);

    Post getById(int id);
}
