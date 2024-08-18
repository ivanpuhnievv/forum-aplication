package com.example.forumapplication.repositories;

import com.example.forumapplication.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    Tag getById(int id);

    Optional<Tag> findByName(String name);
}
