package org.amazinbookstore.controller;

import org.amazinbookstore.model.User;
import org.amazinbookstore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

/**
 * REST controller for managing users within the Amazin Bookstore system.
 * Supports full CRUD operations
 *
 * Typically used for administrative or internal management functions.
 */
public class UserController {

    private final UserService userService;

    /**
     * Creates a new user in the system.
     *
     * @param user the user object to create, validated before processing
     * @return the created {@link User} with HTTP 201 (Created)
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Retrieves a user by its ID.
     *
     * @param id the unique identifier of the user
     * @return the requested {@link User} with HTTP 200 (OK)
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Retrieves all users in the system.
     *
     * @return a list of all {@link User} objects with HTTP 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Updates an existing user.
     *
     * @param id the ID of the user to update
     * @param user the updated user data, validated before processing
     * @return the updated {@link User} with HTTP 200 (OK)
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @Valid @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deletes a user permanently from the system.
     *
     * @param id the ID of the user to delete
     * @return HTTP 204 (No Content) upon successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
