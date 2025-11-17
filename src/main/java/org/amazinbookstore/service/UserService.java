package org.amazinbookstore.service;

import org.amazinbookstore.exception.ResourceNotFoundException;
import org.amazinbookstore.model.User;
import org.amazinbookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for managing user accounts.
 *
 * Handles user creation, retrieval, updates, and deletion along with
 * validation checks for username and email uniqueness. Passwords are
 * always encoded before storage.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user after validating that the username and email
     * are not already in use. The password is encoded before saving.
     *
     * @param user the user to create
     * @return the saved {@link User}
     * @throws IllegalArgumentException if username or email already exists
     */
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        //encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Retrieves a user by ID if they exist; otherwise attempts to create them.
     *
     * This method supports scenarios where an external system may supply a user
     * object with an ID that may or may not exist in the database.
     *
     * @param user user object containing desired information
     * @return existing or newly created user
     */
    public User getOrCreateUser(User user) {
        try {
            return getUserById(user.getId());
        } catch (ResourceNotFoundException e) {

            // check if anyone else has same username under diff ID
            if (userRepository.existsByUsername(user.getUsername())) {
                return userRepository.findByUsername(user.getUsername())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + user.getUsername()));
            }

            //encode password before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id user ID
     * @return the matching {@link User}
     * @throws ResourceNotFoundException if no user exists with the provided ID
     */
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username to search for
     * @return the matching {@link User}
     * @throws ResourceNotFoundException if no user exists with that username
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    /**
     * Retrieves all users in the system.
     *
     * @return list of all {@link User} objects
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Updates an existing user with new information.
     *
     * Performs uniqueness checks for updated username and email.
     *
     * @param id   the ID of the user to update
     * @param user the new user data
     * @return the updated user
     * @throws IllegalArgumentException if new username or email already exists
     */
    public User updateUser(String id, User user) {
        User existingUser = getUserById(id);

        // check if new username is taken
        if (!existingUser.getUsername().equals(user.getUsername()) &&
            userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if new email is taken
        if (!existingUser.getEmail().equals(user.getEmail()) &&
            userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        existingUser.setUsername(user.getUsername());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setRole(user.getRole());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    /**
     * Deletes a user permanently.
     *
     * @param id user ID
     * @throws ResourceNotFoundException if the user does not exist
     */
    public void deleteUser(String id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
