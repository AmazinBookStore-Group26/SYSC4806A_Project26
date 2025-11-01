package org.amazinbookstore.service;

import org.amazinbookstore.exception.ResourceNotFoundException;
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
    private final PasswordEncoder passwordEncoder;

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

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

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

    public void deleteUser(String id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
