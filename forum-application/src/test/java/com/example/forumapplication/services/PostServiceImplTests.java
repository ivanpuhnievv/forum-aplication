package com.example.forumapplication.services;


import com.example.forumapplication.exceptions.AuthorizationException;
import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.mappers.TagMapper;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.Role;
import com.example.forumapplication.models.Tag;
import com.example.forumapplication.models.User;
import com.example.forumapplication.models.dtos.TagDto;
import com.example.forumapplication.repositories.PostRepository;
import com.example.forumapplication.repositories.TagRepository;
import com.example.forumapplication.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.example.forumapplication.helpers.TestHelpers.createMockPost;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getAll_Should_ReturnAllPosts() {
        Post post1 = new Post();
        Post post2 = new Post();
        when(postRepository.findAll()).thenReturn(Arrays.asList(post1, post2));

        List<Post> posts = postService.getAll();

        assertEquals(2, posts.size());
        assertTrue(posts.contains(post1));
        assertTrue(posts.contains(post2));
        verify(postRepository).findAll();
    }

    @Test
    void getById_Should_ReturnPost_WhenPostExists() {
        Post post = new Post();
        int postId = 1;
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.getById(postId);

        assertEquals(post, result);
        verify(postRepository).findById(postId);
    }

    @Test
    void getById_Should_ThrowEntityNotFoundException_WhenPostDoesNotExist() {
        int postId = 1;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.getById(postId));
        verify(postRepository).findById(postId);
    }

    @Test
    void create_Should_SetCurrentUserAsCreator() {
        User currentUser = new User();
        currentUser.setUsername("testUser");

        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("Test Content");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(currentUser);
        when(postRepository.save(post)).thenReturn(post);

        postService.create(post);

        assertEquals(currentUser, post.getCreatedBy());
        verify(postRepository).save(post);
    }

    @Test
    void update_Should_SavePost_WhenUserIsAuthorized() {
        User currentUser = new User();
        currentUser.setId(1);

        Post post = new Post();
        post.setId(1);
        post.setCreatedBy(currentUser);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(currentUser);
        when(postRepository.getById(1)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);

        postService.update(post);

        verify(postRepository).save(post);
    }

    @Test
    void update_Should_ThrowAuthorizationException_WhenUserIsNotAuthorized() {
        User currentUser = new User();
        currentUser.setId(1);

        Post post = new Post();
        post.setId(1);
        post.setCreatedBy(new User()); // Different user

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(currentUser);
        when(postRepository.getById(1)).thenReturn(post);

        assertThrows(AuthorizationException.class, () -> postService.update(post));
    }

    @Test
    void delete_Should_ThrowAuthorizationException_WhenUserIsNotAdmin() {
        User currentUser = new User();
        currentUser.setUsername("testUser");
        Role role = new Role();
        role.setName("USER");
        currentUser.setRole_id(role);

        Post post = new Post();
        post.setId(1);
        post.setCreatedBy(currentUser);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(currentUser);
        when(postRepository.getById(1)).thenReturn(post);

        assertThrows(AuthorizationException.class, () -> postService.delete(1));
    }

    @Test
    void delete_Should_DeletePost_WhenUserIsAdmin() {
        User currentUser = new User();
        currentUser.setUsername("testUser");
        Role role = new Role();
        role.setName("ADMIN");
        currentUser.setRole_id(role);

        Post post = new Post();
        post.setId(1);
        post.setCreatedBy(currentUser);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentUser.getUsername());
        when(userRepository.findByUsername("testUser")).thenReturn(currentUser);
        when(postRepository.getById(1)).thenReturn(post);

        postService.delete(1);

        verify(postRepository).deleteById(1);
    }

    @Test
    void likePost_Should_AddLike_WhenNotAlreadyLiked() {
        User user = new User();
        Post post = new Post();
        post.setLikes(new HashSet<>());

        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        postService.likePost(1, user);

        assertTrue(post.likesContainUser(user));
        verify(postRepository).save(post);
    }

    @Test
    void likePost_Should_RemoveLike_WhenAlreadyLiked() {
        User user = new User();
        Post post = new Post();
        post.setLikes(new HashSet<>());
        post.addLike(user);

        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        postService.likePost(1, user);

        assertFalse(post.likesContainUser(user));
        verify(postRepository).save(post);
    }

    @Test
    void removeLike_Should_RemoveLikeFromPost() {
        User user = new User();
        Post post = new Post();
        post.setLikes(new HashSet<>());
        post.addLike(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        postService.removeLike(1);

        assertFalse(post.likesContainUser(user));
        verify(postRepository).save(post);
    }

//    @Test
//    void addTag_Should_AddTagToPost() {
//        User currentUser = new User();
//        currentUser.setUsername("testUser");
//        Role role = new Role();
//        role.setName("ADMIN");
//        currentUser.setRole_id(role);
//
//        Post post = createMockPost();
//        post.setCreatedBy(currentUser);
//        Tag tag = new Tag();
//        tag.setName("Test Tag");
//        tag.setId(1);
//        post.setId(1);
//        post.setTags(new HashSet<>());
//
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getName()).thenReturn(currentUser.getUsername());
//        when(userRepository.findByUsername("testUser")).thenReturn(currentUser);
//        when(postRepository.getById(1)).thenReturn(post);
//
//        when(tagRepository.getById(1)).thenReturn(tag);
//        when(postRepository.save(post)).thenReturn(post);
//        when(tagRepository.save(tag)).thenReturn(tag);
//        when(tagMapper.fromDto(1, new TagDto())).thenReturn(tag);
//        when(postRepository.save(post)).thenReturn(post);
//
//        postService.addTag(post.getId(), tag.getId());
//
//        assertTrue(post.getTags().contains(tag));
//        assertTrue(tag.getPosts().contains(post));
//        verify(postRepository).save(post);
//        verify(tagRepository).save(tag);
//    }
//
//    @Test
//    void deleteTag_Should_RemoveTagFromPost() {
//        Post post = new Post();
//        Tag tag = new Tag();
//        tag.setId(1);
//        post.setId(1);
//        post.setTags(new HashSet<>());
//        post.getTags().add(tag);
//        tag.getPosts().add(post);
//
//        when(postRepository.findById(1)).thenReturn(Optional.of(post));
//        when(tagRepository.findById(1)).thenReturn(Optional.of(tag));
//        when(postRepository.save(post)).thenReturn(post);
//        when(tagRepository.save(tag)).thenReturn(tag);
//
//        postService.deleteTag(1, 1);
//
//        assertFalse(post.getTags().contains(tag));
//        assertFalse(tag.getPosts().contains(post));
//        verify(postRepository).save(post);
//        verify(tagRepository).save(tag);
//    }
//
//    @Test
//    void changeTag_Should_UpdateTagOnPost() {
//        Post post = new Post();
//        post.setId(1);
//        TagDto tagDto = new TagDto();
//        Tag tag = new Tag();
//        tag.setId(1);
//
//        when(postRepository.findById(1)).thenReturn(Optional.of(post));
//        when(tagMapper.fromDto(1, tagDto)).thenReturn(tag);
//        when(tagRepository.save(tag)).thenReturn(tag);
//        when(postRepository.save(post)).thenReturn(post);
//
//        postService.changeTag(1, 1, tagDto);
//
//        assertTrue(post.getTags().contains(tag));
//        verify(tagRepository).save(tag);
//        verify(postRepository).save(post);
//    }
}
