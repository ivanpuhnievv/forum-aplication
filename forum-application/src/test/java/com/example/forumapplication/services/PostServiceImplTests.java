package com.example.forumapplication.services;

import com.example.forumapplication.exceptions.AuthorizationException;
import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import com.example.forumapplication.repositories.PostRepository;
import com.example.forumapplication.repositories.UserRepository;
import com.example.forumapplication.services.contracts.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.example.forumapplication.helpers.TestHelpers.createMockPost;
import static com.example.forumapplication.helpers.TestHelpers.createMockUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTests {

    @Mock
    PostRepository mockPostRepository;


    @Mock
    UserRepository mockUserRepository;

    @Mock
    UserService mockUserService;

    @InjectMocks
    PostServiceImpl postService;

    @Test
    public void getPostById_Should_ReturnPost_When_MatchExists() {
        // Arrange
        Post mockPost = createMockPost();
        when(mockPostRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));

        // Act
        Post result = postService.getById(mockPost.getId());

        // Assert
        assertNotNull(result);
        assertEquals(mockPost.getId(), result.getId());
        assertEquals(mockPost.getTitle(), result.getTitle());
        verify(mockPostRepository, times(1)).findById(mockPost.getId());
    }

    @Test
    public void getPostById_Should_ThrowEntityNotFoundException_When_NoMatchExists() {
        // Arrange
        int nonExistentId = 99;
        when(mockPostRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> postService.getById(nonExistentId));
        verify(mockPostRepository, times(1)).findById(nonExistentId);
    }

    @Test
    public void getAll_Should_ReturnListOfPosts() {
        // Arrange
        List<Post> mockPosts = List.of(createMockPost(), createMockPost());
        when(mockPostRepository.findAll()).thenReturn(mockPosts);

        // Act
        List<Post> result = postService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(mockPostRepository, times(1)).findAll();
    }
}
