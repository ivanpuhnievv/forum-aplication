package com.example.forumapplication.repositories;

import com.example.forumapplication.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findById(int id);

    Role findByName(String name);
}
