package org.amazinbookstore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a completed purchase order within the Amazin Bookstore system.
 * This entity is stored in the MongoDB orders collection.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    private String userId;

    private List<OrderItem> items = new ArrayList<>();

    private Double totalAmount;

    private LocalDateTime orderDate;

    private OrderStatus status;

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        COMPLETED,
        CANCELLED
    }
}