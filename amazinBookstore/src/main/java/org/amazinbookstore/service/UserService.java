package org.amazinbookstore.service;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.amazinbookstore.model.User;
import org.amazinbookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        return userRepository.save(user);
    }

    public User getOrCreateUser(User user) {
        try {
            return getUserById(user.getId());
        } catch (ResponseStatusException e) {

            // check if username alr exists with a diff ID
            if (userRepository.existsByUsername(user.getUsername())) {
                return userRepository.findByUsername(user.getUsername()).orElse(null);
            }

            return userRepository.save(user);
        }
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with username: " + username));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(String id, User user) {
        User existingUser = getUserById(id);
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setRole(user.getRole());
        return userRepository.save(existingUser);
    }

    public void deleteUser(String id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
