package org.amazinbookstore.service;

import org.amazinbookstore.exception.ResourceNotFoundException;
import org.amazinbookstore.model.User;
import org.amazinbookstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 * Tests user CRUD operations, authentication, and validation logic.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;

    /**
     * Sets up test data before each test.
     * Initializes two sample users with different roles for testing.
     */
    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId("1");
        user1.setUsername("johndoe");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john@example.com");
        user1.setPassword("password123");
        user1.setRole(User.UserRole.CUSTOMER);

        user2 = new User();
        user2.setId("2");
        user2.setUsername("janedoe");
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        user2.setEmail("jane@example.com");
        user2.setPassword("password456");
        user2.setRole(User.UserRole.OWNER);
    }

    /**
     * Tests successfully creating a new user.
     * Should encode the password and save the user to the repository.
     */
    @Test
    void testCreateUser_Success() {
        when(userRepository.existsByUsername(user1.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user1.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user1.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user1);

        User result = userService.createUser(user1);

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        verify(userRepository, times(1)).existsByUsername(user1.getUsername());
        verify(userRepository, times(1)).existsByEmail(user1.getEmail());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Tests creating a user when the username already exists.
     * Should throw IllegalArgumentException and not save the user.
     */
    @Test
    void testCreateUser_UsernameAlreadyExists() {
        when(userRepository.existsByUsername(user1.getUsername())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user1);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername(user1.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Tests creating a user when the email already exists.
     * Should throw IllegalArgumentException and not save the user.
     */
    @Test
    void testCreateUser_EmailAlreadyExists() {
        when(userRepository.existsByUsername(user1.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user1.getEmail())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user1);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(user1.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Tests retrieving a user by ID when the user exists.
     * Should return the user with matching ID.
     */
    @Test
    void testGetUserById_Found() {
        when(userRepository.findById("1")).thenReturn(Optional.of(user1));

        User result = userService.getUserById("1");

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository, times(1)).findById("1");
    }

    /**
     * Tests retrieving a user by ID when the user does not exist.
     * Should throw ResourceNotFoundException.
     */
    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById("999");
        });

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository, times(1)).findById("999");
    }

    /**
     * Tests retrieving a user by username when the user exists.
     * Should return the user with matching username.
     */
    @Test
    void testGetUserByUsername_Found() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user1));

        User result = userService.getUserByUsername("johndoe");

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository, times(1)).findByUsername("johndoe");
    }

    /**
     * Tests retrieving a user by username when the user does not exist.
     * Should throw ResourceNotFoundException.
     */
    @Test
    void testGetUserByUsername_NotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByUsername("unknown");
        });

        assertEquals("User not found with username: unknown", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("unknown");
    }

    /**
     * Tests retrieving all users from the repository.
     * Should return a list of all users.
     */
    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("johndoe", result.get(0).getUsername());
        assertEquals("janedoe", result.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    /**
     * Tests successfully updating an existing user.
     * Should validate new username and email, encode new password, and save changes.
     */
    @Test
    void testUpdateUser_Success() {
        User updatedUser = new User();
        updatedUser.setUsername("johndoe_updated");
        updatedUser.setFirstName("John");
        updatedUser.setLastName("Smith");
        updatedUser.setEmail("john.smith@example.com");
        updatedUser.setPassword("newPassword");
        updatedUser.setRole(User.UserRole.OWNER);

        when(userRepository.findById("1")).thenReturn(Optional.of(user1));
        when(userRepository.existsByUsername("johndoe_updated")).thenReturn(false);
        when(userRepository.existsByEmail("john.smith@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(user1);

        User result = userService.updateUser("1", updatedUser);

        assertNotNull(result);
        verify(userRepository, times(1)).findById("1");
        verify(userRepository, times(1)).existsByUsername("johndoe_updated");
        verify(userRepository, times(1)).existsByEmail("john.smith@example.com");
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Tests updating a user when the new username is already taken by another user.
     * Should throw IllegalArgumentException and not save changes.
     */
    @Test
    void testUpdateUser_UsernameAlreadyTaken() {
        User updatedUser = new User();
        updatedUser.setUsername("janedoe");
        updatedUser.setFirstName("John");
        updatedUser.setLastName("Doe");
        updatedUser.setEmail("john@example.com");
        updatedUser.setRole(User.UserRole.CUSTOMER);

        when(userRepository.findById("1")).thenReturn(Optional.of(user1));
        when(userRepository.existsByUsername("janedoe")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser("1", updatedUser);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername("janedoe");
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Tests updating a user when the new email is already taken by another user.
     * Should throw IllegalArgumentException and not save changes.
     */
    @Test
    void testUpdateUser_EmailAlreadyTaken() {
        User updatedUser = new User();
        updatedUser.setUsername("johndoe");
        updatedUser.setFirstName("John");
        updatedUser.setLastName("Doe");
        updatedUser.setEmail("jane@example.com");
        updatedUser.setRole(User.UserRole.CUSTOMER);

        when(userRepository.findById("1")).thenReturn(Optional.of(user1));
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser("1", updatedUser);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail("jane@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Tests updating a user while keeping the same username and email.
     * Should skip username/email validation and update other fields without encoding empty password.
     */
    @Test
    void testUpdateUser_SameUsernameAndEmail() {
        User updatedUser = new User();
        updatedUser.setUsername("johndoe");
        updatedUser.setFirstName("Johnny");
        updatedUser.setLastName("Doe");
        updatedUser.setEmail("john@example.com");
        updatedUser.setPassword("");
        updatedUser.setRole(User.UserRole.CUSTOMER);

        when(userRepository.findById("1")).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenReturn(user1);

        User result = userService.updateUser("1", updatedUser);

        assertNotNull(result);
        verify(userRepository, times(1)).findById("1");
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Tests successfully deleting an existing user.
     * Should find and remove the user from the repository.
     */
    @Test
    void testDeleteUser_Success() {
        when(userRepository.findById("1")).thenReturn(Optional.of(user1));
        doNothing().when(userRepository).delete(user1);

        userService.deleteUser("1");

        verify(userRepository, times(1)).findById("1");
        verify(userRepository, times(1)).delete(user1);
    }

    /**
     * Tests deleting a user that does not exist.
     * Should throw ResourceNotFoundException and not attempt deletion.
     */
    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser("999");
        });

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository, times(1)).findById("999");
        verify(userRepository, never()).delete(any(User.class));
    }

    /**
     * Tests getting or creating a user when the user already exists by ID.
     * Should return the existing user without creating a new one.
     */
    @Test
    void testGetOrCreateUser_UserExists() {
        when(userRepository.findById("1")).thenReturn(Optional.of(user1));

        User result = userService.getOrCreateUser(user1);

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        verify(userRepository, times(1)).findById("1");
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Tests getting or creating a user when the user does not exist.
     * Should create and save a new user with encoded password.
     */
    @Test
    void testGetOrCreateUser_UserDoesNotExist_CreatesNew() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user1);

        User result = userService.getOrCreateUser(user1);

        assertNotNull(result);
        verify(userRepository, times(1)).findById("1");
        verify(userRepository, times(1)).existsByUsername("johndoe");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Tests getting or creating a user when the username exists under a different ID.
     * Should return the existing user with that username.
     */
    @Test
    void testGetOrCreateUser_UsernameExistsUnderDifferentId() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user1));

        User result = userService.getOrCreateUser(user1);

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        verify(userRepository, times(1)).findById("1");
        verify(userRepository, times(1)).existsByUsername("johndoe");
        verify(userRepository, times(1)).findByUsername("johndoe");
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Tests getting or creating a user when username exists check returns true but user not found.
     * Should throw ResourceNotFoundException due to data inconsistency.
     */
    @Test
    void testGetOrCreateUser_UsernameExistsButNotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getOrCreateUser(user1);
        });

        assertEquals("User not found with username: johndoe", exception.getMessage());
        verify(userRepository, times(1)).findById("1");
        verify(userRepository, times(1)).existsByUsername("johndoe");
        verify(userRepository, times(1)).findByUsername("johndoe");
        verify(userRepository, never()).save(any(User.class));
    }
}
