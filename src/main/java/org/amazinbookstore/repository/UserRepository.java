package org.amazinbookstore.repository;

import org.amazinbookstore.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} documents in MongoDB.
 *
 * Offers convenient lookup methods used in authentication, registration,
 * and uniqueness validation.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return an {@link Optional} containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email.
     *
     * @param email the email address to search for
     * @return an {@link Optional} containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a username already exists in the system.
     *
     * @param username the username to check
     * @return true if a user with the username exists, otherwise false
     */
    boolean existsByUsername(String username);

    /**
     * Checks whether an email address already exists in the system.
     *
     * @param email the email to check
     * @return true if the email is already registered, otherwise false
     */
    boolean existsByEmail(String email);
}
