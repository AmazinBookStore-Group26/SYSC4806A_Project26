package org.amazinbookstore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user's shopping cart stored in MongoDB.
 *
 * The cart is associated with a single user and contains a list of
 * @link CartItem objects. Each item represents a book added to the cart
 * along with its corresponding quantity.
 *
 * The cart provides helper methods for modifying cart contents:
 *  - Adding an item (incrementing quantity if it already exists
 *  - Updating quantity of an existing item
 *  - Removing an item entirely
 *  - Clearing all items
 * This class is persisted inside the shopping_carts collection
 * in MongoDB due to the @link Document annotation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "shopping_carts")
public class ShoppingCart {

    @Id
    private String id;

    @NotBlank(message = "User ID is required")
    private String userId;

    private List<CartItem> items = new ArrayList<>();

    /**
     * Adds an item to the cart. If the item already exists, increments its quantity.
     *
     * @param bookId the ID of the book to add
     * @param quantity the number of copies to add
     */
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

    /**
     * Removes a specific item from the cart entirely.
     *
     * @param bookId the ID of the book to remove
     */
    public void removeItem(String bookId) {
        items.removeIf(item -> item.getBookId().equals(bookId));
    }

    /**
     * Updates the quantity of an existing cart item.
     * If quantity ≤ 0, the item is removed from the cart.
     *
     * @param bookId the ID of the book being updated
     * @param quantity the new quantity to set
     */
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

    /**
     * Clears the cart by removing all items.
     */
    public void clear() {
        items.clear();
    }
}
