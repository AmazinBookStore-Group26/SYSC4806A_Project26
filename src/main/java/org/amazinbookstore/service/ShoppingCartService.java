package org.amazinbookstore.service;

import org.amazinbookstore.exception.ResourceNotFoundException;
import org.amazinbookstore.model.ShoppingCart;
import org.amazinbookstore.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final BookService bookService;

    /**
     * Retrieves the shopping cart associated with the given user ID.
     * If no cart exists, a new one is created automatically.
     *
     * @param userId the ID of the user requesting their cart
     * @return the existing or newly created {@link ShoppingCart}
     */
    public ShoppingCart getCartByUserId(String userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));
    }

    /**
     * Creates a new shopping cart for the specified user.
     *
     * @param userId the ID of the user who the cart will belong to
     * @return the newly created and persisted {@link ShoppingCart}
     */
    public ShoppingCart createCart(String userId) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(userId);
        return shoppingCartRepository.save(cart);
    }

    /**
     * Adds a book to the user's cart.
     * If the book already exists in the cart, its quantity is incremented.
     *
     * @param userId  the ID of the user whose cart is being updated
     * @param bookId  the ID of the book to add
     * @param quantity the quantity to add
     * @return the updated {@link ShoppingCart}
     */
    public ShoppingCart addItemToCart(String userId, String bookId, Integer quantity) {
        // Verify book exists
        bookService.getBookById(bookId);

        ShoppingCart cart = getCartByUserId(userId);
        cart.addItem(bookId, quantity);
        return shoppingCartRepository.save(cart);
    }

    /**
     * Removes a specific book from the user's cart.
     *
     * @param userId the ID of the user whose cart is being modified
     * @param bookId the ID of the book to remove
     * @return the updated {@link ShoppingCart}
     */
    public ShoppingCart removeItemFromCart(String userId, String bookId) {
        ShoppingCart cart = getCartByUserId(userId);
        cart.removeItem(bookId);
        return shoppingCartRepository.save(cart);
    }

    /**
     * Updates the quantity of a specific item in the user's cart.
     * Setting quantity to zero behaves as a remove operation.
     *
     * @param userId  the ID of the user whose cart is being updated
     * @param bookId  the ID of the book whose quantity is being modified
     * @param quantity the new quantity to set
     * @return the updated {@link ShoppingCart}
     */
    public ShoppingCart updateItemQuantity(String userId, String bookId, Integer quantity) {
        ShoppingCart cart = getCartByUserId(userId);
        cart.updateItemQuantity(bookId, quantity);
        return shoppingCartRepository.save(cart);
    }

    /**
     * Clears all items from the user's cart.
     *
     * @param userId the ID of the user whose cart should be emptied
     */
    public void clearCart(String userId) {
        ShoppingCart cart = getCartByUserId(userId);
        cart.clear();
        shoppingCartRepository.save(cart);
    }

    /**
     * Retrieves a shopping cart based on its MongoDB document ID.
     *
     * @param cartId the document ID of the cart
     * @return the corresponding {@link ShoppingCart}
     * @throws ResourceNotFoundException if no cart exists with the given ID
     */
    public ShoppingCart getCartById(String cartId) {
        return shoppingCartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + cartId));
    }
}
