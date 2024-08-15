package com.example.forumapplication.services;

import com.example.forumapplication.exceptions.AuthorizationException;
import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.mappers.TagMapper;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.Tag;
import com.example.forumapplication.models.User;
import com.example.forumapplication.models.dtos.TagDto;
import com.example.forumapplication.repositories.PostRepository;
import com.example.forumapplication.repositories.TagRepository;
import com.example.forumapplication.repositories.UserRepository;
import com.example.forumapplication.services.contracts.CommentService;
import com.example.forumapplication.services.contracts.PostService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private static final String MODIFY_POST_ERROR_MESSAGE = "Only admin or post creator can modify a post.";

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, TagRepository tagRepository, TagMapper tagMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    @Override
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    @Override
    public Post getById(int id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post", id));
    }

    @Override
    public void create(Post post) {
        post.setCreatedBy(getCurrentUser());
        postRepository.save(post);
    }

    @Override
    public void update(Post post) {
        checkUserPermissions(post.getId());
        postRepository.save(post);
    }

    @Override
    public void delete(int id) {
        checkAdminUserPermissions(id);
        postRepository.deleteById(id);
    }

    @Override
    public List<Post> getPostsByUser(User user) {
        return  postRepository.findByCreatedBy(user);
    }

    @Override
    public void likePost(int id) {
        User user = getCurrentUser();
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post", id));
        post.addLike(user);
        postRepository.save(post);
    }

    @Override
    public void removeLike(int id) {
        User user = getCurrentUser();
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post", id));
        post.removeLike(user);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void addTag(int postId, int tagId) {
        checkAdminUserPermissions(postId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post", postId));
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new EntityNotFoundException("Tag", tagId));
        post.getTags().add(tag);
        tag.getPosts().add(post);
        tagRepository.save(tag);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void deleteTag(int postId, int tagId) {
        checkAdminUserPermissions(postId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post", postId));
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new EntityNotFoundException("Tag", tagId));
        post.getTags().remove(tag);
        tag.getPosts().remove(post);
        tagRepository.save(tag);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void changeTag(int postId, int tagId, TagDto tagDto) {
        checkAdminUserPermissions(postId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post", postId));
        Tag tag = tagMapper.fromDto(tagId,tagDto);
        tagRepository.save(tag);
        postRepository.save(post);
    }

    private void checkUserPermissions(int postId) {
        User currentUser = getCurrentUser();
        Post post = postRepository.getById(postId);
        if(!(post.getCreatedBy().equals(currentUser))){
            throw new AuthorizationException(MODIFY_POST_ERROR_MESSAGE);
        }
    }

    private void checkAdminUserPermissions(int id) {
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

    public boolean userHasPosts(User user) {
        return !postRepository.findByCreatedBy(user).isEmpty();
    }

    public Page<Post> findAll(String usernameFilter, String emailFilter, String titleFilter, Pageable pageable) {
        Specification<Post> filters = Specification.where(
                        StringUtils.isEmptyOrWhitespace(usernameFilter) ? null : hasUsername(usernameFilter))
                .and(StringUtils.isEmptyOrWhitespace(emailFilter) ? null : hasEmail(emailFilter))
                .and(StringUtils.isEmptyOrWhitespace(titleFilter) ? null : hasTitle(titleFilter));

        return postRepository.findAll(filters, pageable);
    }

    private Specification<Post> hasUsername(String username) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("createdBy").get("username")), "%" + username.toLowerCase() + "%");
    }

    private Specification<Post> hasEmail(String email) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("createdBy").get("email")), "%" + email.toLowerCase() + "%");
    }

    private Specification<Post> hasTitle(String title) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }
    public List<Post> findByCreatedBy_Id(int userId) {
        return postRepository.findByCreatedBy_Id(userId);
    }
}


