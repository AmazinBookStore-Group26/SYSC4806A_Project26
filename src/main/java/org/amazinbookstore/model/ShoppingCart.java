package org.amazinbookstore.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "shopping_carts")
public class ShoppingCart {

    @Id
    private String id;

    @NotBlank(message = "User ID is required")
    private String userId;

    private List<CartItem> items = new ArrayList<>();

    // Constructors
    public ShoppingCart() {
    }

    // Business methods
    public void addItem(String bookId, Integer quantity) {
        // Check if item already exists in cart
        for (CartItem item : items) {
            if (item.getBookId().equals(bookId)) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        // If not exists, add new item
        items.add(new CartItem(bookId, quantity));
    }

    public void removeItem(String bookId) {
        items.removeIf(item -> item.getBookId().equals(bookId));
    }

    public void updateItemQuantity(String bookId, Integer quantity) {
        for (CartItem item : items) {
            if (item.getBookId().equals(bookId)) {
                if (quantity <= 0) {
                    removeItem(bookId);
                } else {
                    item.setQuantity(quantity);
                }
                return;
            }
        }
    }

    public void clear() {
        items.clear();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}
