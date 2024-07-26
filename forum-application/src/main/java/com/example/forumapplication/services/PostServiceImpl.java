package com.example.forumapplication.services;

import com.example.forumapplication.exceptions.AuthorizationException;
import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import com.example.forumapplication.repositories.PostRepository;
import com.example.forumapplication.repositories.UserRepository;
import com.example.forumapplication.services.contracts.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private static final String MODIFY_POST_ERROR_MESSAGE = "Only admin or post creator can modify a post.";

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Post> get() {
        return postRepository.findAll();
    }

    @Override
    public Post getById(int id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post", id));
    }

    @Override
    public void create(Post post, User user) {
        post.setCreatedBy(user);
        postRepository.save(post);
    }

    @Override
    public void update(Post post) {
        checkModifyPermissions(post.getId());
        postRepository.save(post);
    }

    @Override
    public void delete(int id) {
        checkDeletePermissions(id);
        postRepository.deleteById(id);
    }

    @Override
    public List<Post> getPostsByUser(User user) {
        return  postRepository.findByCreatedBy(user);
    }

    @Override
    public void likePost(int id) {
        User user = getCurrentUser();
        Post post = postRepository.getById(id);
        post.addLike(user);
        postRepository.save(post);
    }

    @Override
    public void removeLike(int id) {
        User user = getCurrentUser();
        Post post = postRepository.getById(id);
        post.removeLike(user);
        postRepository.save(post);
    }

    private void checkModifyPermissions(int postId) {
        User currentUser = getCurrentUser();
        Post post = postRepository.getById(postId);
        if(!(post.getCreatedBy().equals(currentUser))){
            throw new AuthorizationException(MODIFY_POST_ERROR_MESSAGE);
        }
    }

    private void checkDeletePermissions(int id) {
       User currentUser = getCurrentUser();
       Post post = postRepository.getById(id);
       if(!(currentUser.getRole_id().getName().equalsIgnoreCase("ADMIN")) || !(post.getCreatedBy().equals(currentUser))){
           throw new AuthorizationException(MODIFY_POST_ERROR_MESSAGE);
       }
    }

    private User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(authentication.getName());
        return currentUser;
    }
}
