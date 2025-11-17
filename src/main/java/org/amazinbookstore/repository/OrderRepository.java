package org.amazinbookstore.repository;

import org.amazinbookstore.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for accessing and managing {@link Order} documents in MongoDB.
 *
 * Provides convenience query methods used for retrieving user-specific order history.
 */
@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    /**
     * Retrieves all orders placed by a specific user.
     *
     * @param userId the ID of the user
     * @return list of all orders associated with the user
     */
    List<Order> findByUserId(String userId);

    /**
     * Retrieves all orders for a user, sorted with the most recent first.
     *
     * @param userId the ID of the user
     * @return list of user's orders sorted by order date descending
     */
    List<Order> findByUserIdOrderByOrderDateDesc(String userId);
}
