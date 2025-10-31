package org.amazinbookstore.service;

import org.amazinbookstore.exception.ResourceNotFoundException;
import org.amazinbookstore.model.ShoppingCart;
import org.amazinbookstore.repository.ShoppingCartRepository;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final BookService bookService;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, BookService bookService) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.bookService = bookService;
    }

    public ShoppingCart getCartByUserId(String userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));
    }

    public ShoppingCart createCart(String userId) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(userId);
        return shoppingCartRepository.save(cart);
    }

    public ShoppingCart addItemToCart(String userId, String bookId, Integer quantity) {
        // Verify book exists
        bookService.getBookById(bookId);

        ShoppingCart cart = getCartByUserId(userId);
        cart.addItem(bookId, quantity);
        return shoppingCartRepository.save(cart);
    }

    public ShoppingCart removeItemFromCart(String userId, String bookId) {
        ShoppingCart cart = getCartByUserId(userId);
        cart.removeItem(bookId);
        return shoppingCartRepository.save(cart);
    }

    public ShoppingCart updateItemQuantity(String userId, String bookId, Integer quantity) {
        ShoppingCart cart = getCartByUserId(userId);
        cart.updateItemQuantity(bookId, quantity);
        return shoppingCartRepository.save(cart);
    }

    public void clearCart(String userId) {
        ShoppingCart cart = getCartByUserId(userId);
        cart.clear();
        shoppingCartRepository.save(cart);
    }

    public ShoppingCart getCartById(String cartId) {
        return shoppingCartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + cartId));
    }
}
