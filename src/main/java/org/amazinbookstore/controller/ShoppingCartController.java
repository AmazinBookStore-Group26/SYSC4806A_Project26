package org.amazinbookstore.controller;

import org.amazinbookstore.dto.AddToCartRequest;
import org.amazinbookstore.model.ShoppingCart;
import org.amazinbookstore.service.ShoppingCartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    /**
     * Retrieves the shopping cart associated with a given user.
     *
     * @param userId the ID of the user whose cart should be returned
     * @return the user's shopping cart
     *
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ShoppingCart> getCart(@PathVariable String userId) {
        ShoppingCart cart = shoppingCartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    /**
     * Adds an item to the user's shopping cart. If the item already exists, its quantity is incremented.
     *
     * @param userId  the ID of the user whose cart is being modified
     * @param request the payload containing the bookId and quantity to add
     * @return the updated shopping cart
     */
    @PostMapping("/{userId}/items")
    public ResponseEntity<ShoppingCart> addItemToCart(
            @PathVariable String userId,
            @Valid @RequestBody AddToCartRequest request
    ) {
        ShoppingCart cart = shoppingCartService.addItemToCart(
                userId,
                request.getBookId(),
                request.getQuantity()
        );
        return ResponseEntity.ok(cart);
    }

    /**
     * Removes a specific book from the user's cart entirely.
     *
     * @param userId the ID of the user whose cart is being modified
     * @param bookId the ID of the book to remove
     * @return the updated shopping cart
     */
    @DeleteMapping("/{userId}/items/{bookId}")
    public ResponseEntity<ShoppingCart> removeItemFromCart(
            @PathVariable String userId,
            @PathVariable String bookId
    ) {
        ShoppingCart cart = shoppingCartService.removeItemFromCart(userId, bookId);
        return ResponseEntity.ok(cart);
    }

    /**
     * Updates the quantity of a specific item in the user's shopping cart.
     * Setting quantity to zero effectively removes the item from the cart.
     *
     * @param userId   the ID of the user whose cart is being modified
     * @param bookId   the ID of the book whose quantity is being updated
     * @param quantity the new quantity for the item
     * @return the updated shopping cart
     */
    @PutMapping("/{userId}/items/{bookId}")
    public ResponseEntity<ShoppingCart> updateItemQuantity(
            @PathVariable String userId,
            @PathVariable String bookId,
            @RequestParam Integer quantity
    ) {
        ShoppingCart cart = shoppingCartService.updateItemQuantity(userId, bookId, quantity);
        return ResponseEntity.ok(cart);
    }

    /**
     * Removes all items from the user's shopping cart.
     *
     * @param userId the ID of the user whose cart is being cleared
     * @return an empty 204 No Content response
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable String userId) {
        shoppingCartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
