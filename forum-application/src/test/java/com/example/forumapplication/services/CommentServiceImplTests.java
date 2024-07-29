package com.example.forumapplication.services;

import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.helpers.TestHelpers;
import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import com.example.forumapplication.repositories.CommentRepository;
import com.example.forumapplication.repositories.PostRepository;
import com.example.forumapplication.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static com.example.forumapplication.helpers.TestHelpers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceImplTests {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void getCommentById_ReturnsComment_WhenCommentExists() {
        Comment comment = new Comment();
        when(commentRepository.findCommentById(1)).thenReturn(comment);

        Comment result = commentService.getCommentById(1);

        assertEquals(comment, result);
    }

    @Test
    void getCommentById_ThrowsEntityNotFoundException_WhenCommentDoesNotExist() {
        when(commentRepository.findCommentById(1)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> commentService.getCommentById(1));
    }

    @Test
    void addComment_AddsCommentToPost() {
        User user = createMockUser();
        Post post = createMockPost();
        Comment comment = createMockComment();
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(postRepository.getById(1)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);

        Post result = commentService.addComment(1, comment);

        assertTrue(result.getComments().contains(comment));
        verify(postRepository).save(post);
    }

    @Test
    void updateComment_UpdatesCommentContent() {
        Comment existingComment = createMockComment();
        existingComment.setContent("Old Content");
        Comment updatedComment = createMockComment();
        updatedComment.setContent("New Content");
        when(commentRepository.findCommentById(1)).thenReturn(existingComment);
        when(commentRepository.save(existingComment)).thenReturn(updatedComment);

        Comment result = commentService.updateComment(1, updatedComment);

        assertEquals("New Content", result.getContent());
        verify(commentRepository).save(existingComment);
    }

    @Test
    void deleteComment_DeletesComment_WhenCommentExists() {
        Comment comment = createMockComment();
        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1);

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_ThrowsEntityNotFoundException_WhenCommentDoesNotExist() {
        when(commentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(1));
    }

    @Test
    void addReply_AddsReplyToComment() {
        User user = createMockUser();
        Comment parentComment = createMockComment();
        Comment reply = createMockComment();
        when(authentication.getName()).thenReturn("username");
        when(userRepository.findByUsername("username")).thenReturn(user);
        when(commentRepository.findCommentById(1)).thenReturn(parentComment);
        when(commentRepository.save(parentComment)).thenReturn(reply);

        Comment result = commentService.addReply(1, reply);

        assertTrue(parentComment.getReplies().contains(reply));
        verify(commentRepository).save(parentComment);
    }
}