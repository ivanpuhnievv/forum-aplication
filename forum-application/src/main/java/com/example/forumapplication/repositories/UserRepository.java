package com.example.forumapplication.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.forumapplication.models.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Page<User> findAll(Specification<User> filters, Pageable pageable);
    List<User> findListByUsername(String username);
    User findByUsername(String username);
    User findByEmail(String email);
    User findByUsernameOrEmail(String username, String email);
    User findById(int userId);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
