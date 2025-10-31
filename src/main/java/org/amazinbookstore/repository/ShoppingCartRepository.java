package org.amazinbookstore.repository;

import org.amazinbookstore.model.ShoppingCart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends MongoRepository<ShoppingCart, String> {

    Optional<ShoppingCart> findByUserId(String userId);

    void deleteByUserId(String userId);
}
