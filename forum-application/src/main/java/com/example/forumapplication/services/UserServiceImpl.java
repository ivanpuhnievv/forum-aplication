package com.example.forumapplication.services;

import com.example.forumapplication.exceptions.EntityDuplicateException;
import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.exceptions.UnauthorizedException;
import com.example.forumapplication.models.Role;
import com.example.forumapplication.models.User;
import com.example.forumapplication.repositories.RoleRepository;
import com.example.forumapplication.repositories.UserRepository;
import com.example.forumapplication.services.contracts.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.forumapplication.filters.specifications.UserSpecifications.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    private final PasswordEncoder passwordEncoder;


    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
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

    public void processOAuthPostLogin(String email, String username) {
        User existUser = userRepository.findByEmail(email);

        if (existUser == null) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(username);
           try {
               checkUsernameUnique(newUser);
           } catch (EntityDuplicateException e) {
             String uniqueUsername = setUserNameUnique(username);
               newUser.setUsername(uniqueUsername);
           }
            Role role = roleRepository.findByName("USER");
            newUser.setRole_id(role);
            userRepository.save(newUser);
        }
    }

    private String setUserNameUnique(String username) {
        String newUsername = username;
        int i = 1;
        while (userRepository.findByUsername(newUsername) != null) {
            newUsername = username + i;
            i++;
        }
        return newUsername;
    }

    public boolean authenticateUser(String rawPassword, String storedHashedPassword) {
        return passwordEncoder.matches(rawPassword, storedHashedPassword);
    }

    @Override
    public User createUser(User user) {
        checkUsernameUnique(user);
        checkEmailUnique(user);
        Role role = roleRepository.findByName("USER");
        String rawPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);
        user.setRole_id(role);
        return userRepository.save(user);
    }

    @Override
    public void createUserWithRole(User user, String roleString) {
            Role role = roleRepository.findByName(roleString);
            if (role == null) {
                throw new EntityNotFoundException("Role", "name", roleString);
            }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName());

        if (currentUser.getRole_id().getName().equals("ADMIN")) {
            String rawPassword = user.getPassword();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setPassword(encodedPassword);
            user.setRole_id(role);
            userRepository.save(user);
        } else {
            throw new UnauthorizedException("Only admins can register users with specific roles.");
        }
    }

    @Override
    public User updateUser(User userToUpdate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName());
        if (currentUser.getId() != userToUpdate.getId()) {
            throw new UnauthorizedException("You are not authorized to update this user.");
        }
        if (!userToUpdate.getEmail().equals(currentUser.getEmail()) ||
                !userToUpdate.getUsername().equals(currentUser.getUsername())) {

        checkEmailUnique(userToUpdate);
        checkUsernameUnique(userToUpdate);
        }
        userRepository.save(userToUpdate);

        return userToUpdate;
    }

    @Override
    public void deleteUser(int id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName());
        User userToDelete = findUserById(id);
        if (userToDelete == null) {
            throw new EntityNotFoundException("User", id);
        }
        if (currentUser.getId() != userToDelete.getId() && !currentUser.getRole_id().getName().equals("ADMIN")) {
            throw new UnauthorizedException("You are not authorized to delete this user.");
        }

        userRepository.delete(userToDelete);
    }

    private void checkEmailUnique(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new EntityDuplicateException("User", "email", user.getEmail());
        }
    }

    private void checkUsernameUnique(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new EntityDuplicateException("User", "username", user.getUsername());
        }
    }

    public User blockUser(User user) {
        user.setBlocked(true);
        return userRepository.save(user);
    }

    public User unblockUser(User user) {
        if (!user.isBlocked()) {
            throw new UnauthorizedException("User is not blocked.");
        }
        user.setBlocked(false);
        return userRepository.save(user);
    }

    public void uploadPhoto(User userToUpdate, String photo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName());
        if (currentUser.getId() != userToUpdate.getId()) {
            throw new UnauthorizedException("You are not authorized to update this user.");
        }
        userToUpdate.setProfilePhoto(photo);
        userRepository.save(userToUpdate);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void updateUserProfilePhoto(String username, String fileName) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setProfilePhoto(fileName);
            userRepository.save(user);
        }
    }

    public void banUser(User user) {
        user.setBanned(true);
        userRepository.save(user);
    }

    public void unbanUser(User user) {
        user.setBanned(false);
        userRepository.save(user);
    }

    public void changeRole(User user, Role role) {
        user.setRole_id(role);
        userRepository.save(user);
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name);
    }
}
