package org.amazinbookstore.service;

import org.amazinbookstore.model.Book;
import org.amazinbookstore.model.ShoppingCart;
import org.amazinbookstore.repository.ShoppingCartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private ShoppingCartService shoppingCartService;

    private ShoppingCart testCart;
    private Book testBook;

    /**
     * Initializes reusable test objects before each test case.
     * Ensures no state is leaked between tests.
     */
    @BeforeEach
    void setUp() {
        // Initialize an empty shopping cart
        testCart = new ShoppingCart();
        testCart.setId("cart1");
        testCart.setUserId("user1");
        testCart.setItems(new ArrayList<>());

        // Initialize a book object, mocking BookService
        testBook = new Book();
        testBook.setId("book1");
        testBook.setTitle("Test Book");
        testBook.setPrice(BigDecimal.valueOf(29.99));
    }

    /**
     * Ensures that when a cart exists in the repository, it is returned as is.
     */
    @Test
    void getCartByUserId_WhenCartExists_ShouldReturnCart() {
        // Mock repository to return an existing cart
        when(shoppingCartRepository.findByUserId("user1")).thenReturn(Optional.of(testCart));

        // Execute service method
        ShoppingCart result = shoppingCartService.getCartByUserId("user1");

        // Validate returned cart
        assertNotNull(result);
        assertEquals("user1", result.getUserId());

        // Verify correct repository call
        verify(shoppingCartRepository, times(1)).findByUserId("user1");
    }

    /**
     * Ensures that when no cart exists, a new one is created and saved.
     */
    @Test
    void getCartByUserId_WhenCartDoesNotExist_ShouldCreateNewCart() {
        // Mock empty lookup
        when(shoppingCartRepository.findByUserId("user1")).thenReturn(Optional.empty());

        // Mock save returning the testCart
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);

        ShoppingCart result = shoppingCartService.getCartByUserId("user1");

        assertNotNull(result);

        // Verify save() was triggered once due to missing cart
        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
    }

    /**
     * Ensures that adding an item calls BookService for validation
     * and saves the updated cart.
     */
    @Test
    void addItemToCart_ShouldAddItem() {
        // Mock book lookup
        when(bookService.getBookById("book1")).thenReturn(testBook);

        // Mock cart lookup and save
        when(shoppingCartRepository.findByUserId("user1")).thenReturn(Optional.of(testCart));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);

        ShoppingCart result = shoppingCartService.addItemToCart("user1", "book1", 2);

        assertNotNull(result);

        // Verify the cart was saved after modification
        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
    }

    /**
     * Ensures that removing an item modifies the cart and results in a save operation.
     */
    @Test
    void removeItemFromCart_ShouldRemoveItem() {
        // Preload cart with an item
        testCart.addItem("book1", 2);

        // Mock repository
        when(shoppingCartRepository.findByUserId("user1")).thenReturn(Optional.of(testCart));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);

        ShoppingCart result = shoppingCartService.removeItemFromCart("user1", "book1");

        assertNotNull(result);

        // Verify persistence
        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
    }

    /**
     * Ensures that clearing all items triggers a repository save.
     */
    @Test
    void clearCart_ShouldClearAllItems() {
        // Preload the cart with one item
        testCart.addItem("book1", 2);

        // Mock repository
        when(shoppingCartRepository.findByUserId("user1")).thenReturn(Optional.of(testCart));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(testCart);

        shoppingCartService.clearCart("user1");

        // Ensure save() was called
        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));

        // Ensure items list is actually empty
        assertTrue(testCart.getItems().isEmpty());
    }

}
