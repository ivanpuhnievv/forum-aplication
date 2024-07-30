package com.example.forumapplication.services;
import com.example.forumapplication.exceptions.EntityDuplicateException;
import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.exceptions.UnauthorizedException;
import com.example.forumapplication.models.Role;
import com.example.forumapplication.models.User;
import com.example.forumapplication.repositories.RoleRepository;
import com.example.forumapplication.repositories.UserRepository;
import com.example.forumapplication.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

import static com.example.forumapplication.helpers.TestHelpers.createMockUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void findUserById_ThrowsEntityNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(1)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userService.findUserById(1));
    }

    @Test
    void findUserByUsername_ThrowsEntityNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findByUsername("username")).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userService.findUserByUsername("username"));
    }

    @Test
    void createUser_EncodesPasswordAndSetsRole() {
        User user = new User();
        user.setPassword("rawPassword");
        user.setEmail("test@example.com");
        user.setUsername("username");

        Role role = new Role();
        role.setName("USER");

        when(roleRepository.findByName("USER")).thenReturn(role);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);

        User createdUser = userService.createUser(user);

        assertEquals("encodedPassword", createdUser.getPassword());
        assertEquals(role, createdUser.getRole_id());
    }

    @Test
    void createUserWithRole_ThrowsUnauthorizedException_WhenUserIsNotAdmin() {
        User user = createMockUser();
        user.getRole_id().setName("USER");

        Role role = new Role();
        role.setName("USER");

        when(authentication.getName()).thenReturn("nonAdminUser");
        when(userRepository.findByUsername("nonAdminUser")).thenReturn(user);
        when(roleRepository.findByName("ADMIN")).thenReturn(role);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertThrows(UnauthorizedException.class, () -> userService.createUserWithRole(user, "ADMIN"));
    }

    @Test
    void deleteUser_ThrowsEntityNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(1)).thenReturn(null);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(1));
    }

    @Test
    void deleteUser_ThrowsUnauthorizedException_WhenUserIsNotAuthorized() {
        User userToDelete = new User();
        userToDelete.setId(1);
        Role userRole = new Role();
        userRole.setName("USER");
        userToDelete.setRole_id(userRole);

        User currentUser = new User();
        currentUser.setId(2);
        Role currentUserRole = new Role();
        currentUserRole.setName("USER");
        currentUser.setRole_id(currentUserRole);

        when(authentication.getName()).thenReturn("currentUser");
        when(userRepository.findByUsername("currentUser")).thenReturn(currentUser);
        when(userRepository.findById(1)).thenReturn(userToDelete);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertThrows(UnauthorizedException.class, () -> userService.deleteUser(1));
    }

    @Test
    void deleteUser_DeletesUser_WhenUserIsAdmin() {
        User userToDelete = new User();
        userToDelete.setId(1);
        Role userRole = new Role();
        userRole.setName("USER");
        userToDelete.setRole_id(userRole);

        User currentUser = new User();
        currentUser.setId(2);
        Role currentUserRole = new Role();
        currentUserRole.setName("ADMIN");
        currentUser.setRole_id(currentUserRole);

        when(authentication.getName()).thenReturn("currentUser");
        when(userRepository.findByUsername("currentUser")).thenReturn(currentUser);
        when(userRepository.findById(1)).thenReturn(userToDelete);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        userService.deleteUser(1);

        verify(userRepository, times(1)).delete(userToDelete);
    }

    @Test
    void findAll_ReturnsPageOfUsers() {
        Pageable pageable = mock(Pageable.class);
        Page<User> userPage = mock(Page.class);
        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);

        Page<User> result = userService.findAll("username", "email", "firstName", pageable);

        assertEquals(userPage, result);
    }


    @Test
    void findUserById_ReturnsUser_WhenUserExists() {
        User user = new User();
        when(userRepository.findById(1)).thenReturn(user);

        User result = userService.findUserById(1);

        assertEquals(user, result);
    }

    @Test
    void findUserByEmail_ThrowsEntityNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findByEmail("email@example.com")).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userService.findUserByEmail("email@example.com"));
    }

    @Test
    void findUserByEmail_ReturnsUser_WhenUserExists() {
        User user = new User();
        when(userRepository.findByEmail("email@example.com")).thenReturn(user);

        User result = userService.findUserByEmail("email@example.com");

        assertEquals(user, result);
    }


    @Test
    void findUserByUsername_ReturnsUser_WhenUserExists() {
        User user = new User();
        when(userRepository.findByUsername("username")).thenReturn(user);

        User result = userService.findUserByUsername("username");

        assertEquals(user, result);
    }

    @Test
    void authenticateUser_ReturnsTrue_WhenPasswordMatches() {
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

        boolean result = userService.authenticateUser("rawPassword", "encodedPassword");

        assertTrue(result);
    }

    @Test
    void authenticateUser_ReturnsFalse_WhenPasswordDoesNotMatch() {
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(false);

        boolean result = userService.authenticateUser("rawPassword", "encodedPassword");

        assertFalse(result);
    }

    @Test
    void createUserWithRole_ThrowsEntityNotFoundException_WhenRoleDoesNotExist() {
        User user = new User();
        when(roleRepository.findByName("ADMIN")).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userService.createUserWithRole(user, "ADMIN"));
    }


    @Test
    void createUserWithRole_CreatesUser_WhenUserIsAdmin() {
        User user = new User();
        user.setPassword("rawPassword");
        Role role = new Role();
        role.setName("ADMIN");
        Role userRole = new Role();
        userRole.setName("ADMIN");
        User currentUser = new User();
        currentUser.setRole_id(userRole);

        when(roleRepository.findByName("ADMIN")).thenReturn(role);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("currentUser");
        when(userRepository.findByUsername("currentUser")).thenReturn(currentUser);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        userService.createUserWithRole(user, "ADMIN");

        assertEquals("encodedPassword", user.getPassword());
        assertEquals(role, user.getRole_id());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_ThrowsUnauthorizedException_WhenUserIsNotAuthorized() {
        User userToUpdate = createMockUser();
        userToUpdate.setUsername("userToUpdate");
        userToUpdate.setId(1);
        User currentUser = createMockUser();
        currentUser.setUsername("currentUser");
        currentUser.getRole_id().setName("USER");
        currentUser.setId(2);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("currentUser");
        when(userRepository.findByUsername("currentUser")).thenReturn(currentUser);

        assertThrows(UnauthorizedException.class, () -> userService.updateUser(userToUpdate));
    }

    @Test
    void updateUser_UpdatesUser_WhenUserIsAuthorized() {
        User userToUpdate = new User();
        userToUpdate.setId(1);
        userToUpdate.setEmail("new@example.com");
        userToUpdate.setUsername("newusername");
        User currentUser = new User();
        currentUser.setId(1);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("currentUser");
        when(userRepository.findByUsername("currentUser")).thenReturn(currentUser);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("newusername")).thenReturn(false);

        User updatedUser = userService.updateUser(userToUpdate);

        assertEquals(userToUpdate, updatedUser);
        verify(userRepository, times(1)).save(userToUpdate);
    }

    @Test
    void blockUser_BlocksUser() {
        User user = new User();
        user.setBlocked(false);

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.blockUser(user);

        assertTrue(result.isBlocked());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void unblockUser_UnblocksUser() {
        User user = new User();
        user.setBlocked(true);

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.unblockUser(user);

        assertFalse(result.isBlocked());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void unblockUser_ThrowsUnauthorizedException_WhenUserIsNotBlocked() {
        User user = new User();
        user.setBlocked(false);

        assertThrows(UnauthorizedException.class, () -> userService.unblockUser(user));
    }

    @Test
    void uploadPhoto_ThrowsUnauthorizedException_WhenUserIsNotAuthorized() {
        User userToUpdate = new User();
        userToUpdate.setId(1);
        User currentUser = new User();
        currentUser.setId(2);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("currentUser");
        when(userRepository.findByUsername("currentUser")).thenReturn(currentUser);

        assertThrows(UnauthorizedException.class, () -> userService.uploadPhoto(userToUpdate, "photo"));
    }

    @Test
    void uploadPhoto_UpdatesUserPhoto_WhenUserIsAuthorized() {
        User userToUpdate = new User();
        userToUpdate.setId(1);
        User currentUser = new User();
        currentUser.setId(1);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("currentUser");
        when(userRepository.findByUsername("currentUser")).thenReturn(currentUser);
        when(userRepository.save(userToUpdate)).thenReturn(userToUpdate);

        userService.uploadPhoto(userToUpdate, "photo");

        assertEquals("photo", userToUpdate.getProfilePhoto());
        verify(userRepository, times(1)).save(userToUpdate);
    }

}