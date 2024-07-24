package com.example.forumapplication.services;

import com.example.forumapplication.exceptions.AuthorizationException;
import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import com.example.forumapplication.repositories.CommentRepository;
import com.example.forumapplication.repositories.PostRepository;
import com.example.forumapplication.repositories.UserRepository;
import com.example.forumapplication.services.contracts.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private static final String MODIFY_POST_ERROR_MESSAGE = "Only admin or post creator can modify a post.";

    private final PostRepository repository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    @Autowired
    public PostServiceImpl(PostRepository repository, UserRepository userRepository, CommentRepository commentRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Post> get() {
        return repository.findAll();
    }

    @Override
    public Post getById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post", id));
    }

    @Override
    public Post getByTitle(String title) {
        return repository.findByTitle(title)
                .orElseThrow(() -> new EntityNotFoundException("Post", 0));
    }

    @Override
    public void create(Post post, User user) {
        checkCreatePermissions(user);
        post.setCreatedBy(user);
        repository.save(post);
    }

    @Override
    public void update(Post post, User user) {
        checkModifyPermissions(post.getId(), user);
        repository.save(post);
    }

    @Override
    public void delete(int id, User user) {
        checkDeletePermissions(id, user);
        repository.deleteById(id);
    }

    @Override
    public void addComment(int postId, int commentId) {
        Post post = repository.getById(postId);
        Comment comment = commentRepository.getById(commentId);
        post.getComments().add(comment);
        repository.save(post);
    }



    private void checkCreatePermissions(User user) {
        if (!(userRepository.existsById(user.getId()))) {
            throw new AuthorizationException(MODIFY_POST_ERROR_MESSAGE);
        }
    }

    private void checkModifyPermissions(int postId, User user) {
        Post post = repository.getById(postId);
        if (!(post.getCreatedBy().equals(user))) {
            throw new AuthorizationException(MODIFY_POST_ERROR_MESSAGE);
        }
    }

    private void checkDeletePermissions(int postId, User user) {
        Post post = repository.getById(postId);
        if (!(user.getRole_id().getName().equals("Admin")) || !(post.getCreatedBy().equals(user))) {
            throw new AuthorizationException(MODIFY_POST_ERROR_MESSAGE);
        }
    }


}
