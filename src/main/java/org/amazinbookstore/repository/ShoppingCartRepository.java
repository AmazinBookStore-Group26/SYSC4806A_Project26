package org.amazinbookstore.repository;

import org.amazinbookstore.model.ShoppingCart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on @link ShoppingCart documents stored in MongoDB.
 *
 * This repository provides data access methods via @link MongoRepository and custom finder methods for retrieving
 * and deleting carts by user ID.
 *
 * A ShoppingCart is uniquely associated with a specific user, so {@code userId} queries are used for cart lookup and cleanup
 */
@Repository
public interface ShoppingCartRepository extends MongoRepository<ShoppingCart, String> {

    /**
     * Retrieves the shopping cart associated with the given user ID.
     *
     * @param userId the ID of the user whose cart should be retrieved
     * @return an {@link Optional} containing the cart if found, or empty if the user has no cart
     */
    Optional<ShoppingCart> findByUserId(String userId);

    /**
     * Deletes the shopping cart belonging to the specified user.
     *
     * @param userId the ID of the user whose cart should be deleted
     */
    void deleteByUserId(String userId);
}
