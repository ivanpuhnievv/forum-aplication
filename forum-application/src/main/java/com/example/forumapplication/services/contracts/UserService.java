package com.example.forumapplication.services.contracts;

import com.example.forumapplication.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    Page<User> findAll(String usernameFilter, String emailFilter, String firstNameFilter, Pageable pageable);
    User findUserById(int id);
    User findUserByEmail(String email);
    User findUserByUsername(String username);

    User createUser(User user);
    User updateUser(User user);
    void deleteUser(int id);

}
