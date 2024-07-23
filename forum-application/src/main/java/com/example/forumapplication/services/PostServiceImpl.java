package com.example.forumapplication.services;

import com.example.forumapplication.exceptions.AuthorizationException;
import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import com.example.forumapplication.repositories.PostRepository;
import com.example.forumapplication.services.contracts.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private static final String MODIFY_POST_ERROR_MESSAGE = "Only admin or post creator can modify a post.";

    private final PostRepository repository;

    @Autowired
    public PostServiceImpl(PostRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Post> get() {
        return repository.findAll();
    }

    @Override
    public Post getById(int id) {
        Post post = repository.getById(id);
        if (post == null) {
            throw new EntityNotFoundException("Post", id);
        }
        return post;
    }

    @Override
    public Post getByTitle(String title) {
        Post post = repository.getByTitle(title);
        if (post == null) {
            throw new EntityNotFoundException("Post", post.getId());
        }
        return post;
    }

    @Override
    public void create(Post post, User user) {
        post.setCreatedBy(user);
        repository.create(post);
    }

    @Override
    public void update(Post post, User user) {
        checkModifyPermissions(post.getId(), user);
        repository.update(post);
    }

    @Override
    public void delete(int id, User user) {
        checkModifyPermissions(id, user);
        repository.delete(id);
    }

    private void checkModifyPermissions(int postId, User user) {
        Post post = repository.getById(postId);
        if (!(user.getRole_id().getRole().equals("Admin")) || !(post.getCreatedBy().equals(user))) {
            throw new AuthorizationException(MODIFY_POST_ERROR_MESSAGE);
        }
    }


}
