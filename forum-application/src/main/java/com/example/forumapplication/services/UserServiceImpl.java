package com.example.forumapplication.services;

import com.example.forumapplication.exceptions.EntityDuplicateException;
import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.models.User;
import com.example.forumapplication.repositories.UserRepository;
import com.example.forumapplication.services.contracts.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import static com.example.forumapplication.filters.specifications.UserSpecifications.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<User> findAll(String usernameFilter, String emailFilter, String firstNameFilter, Pageable pageable) {
        Specification<User> filters = Specification.where(StringUtils.isEmptyOrWhitespace(usernameFilter) ? null : username(usernameFilter))
                .and(StringUtils.isEmptyOrWhitespace(firstNameFilter) ? null : firstName(firstNameFilter))
                .and(StringUtils.isEmptyOrWhitespace(emailFilter) ? null : email(emailFilter));
        return userRepository.findAll(filters, pageable);
    }

    @Override
    public User findUserById(int id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new EntityNotFoundException("User", id);
        }
        return user;
    }

    @Override
    public User findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("User", "email", email);
        }
        return user;
    }

    @Override
    public User findUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User", "username", username);
        }
        return user;
    }

    @Override
    public User createUser(User user) {
        checkNameUnique(user);
        checkEmailUnique(user);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User userToUpdate) {
        checkEmailUnique(userToUpdate);
        checkNameUnique(userToUpdate);
        userRepository.save(userToUpdate);

        return userToUpdate;
    }

    @Override
    public void deleteUser(int id) {
        User userToDelete = findUserById(id);
        if (userToDelete == null) {
            throw new EntityNotFoundException("User", id);
        }
        userRepository.delete(userToDelete);
    }

    private void checkEmailUnique(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityDuplicateException("User", "email", user.getEmail());
        }
    }

    private void checkNameUnique(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new EntityNotFoundException("User", "username", user.getUsername());
        }
    }
}
