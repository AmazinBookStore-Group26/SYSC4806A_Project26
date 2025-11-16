package org.amazinbookstore.controller;

import org.amazinbookstore.dto.AddToCartRequest;
import org.amazinbookstore.model.ShoppingCart;
import org.amazinbookstore.service.ShoppingCartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartControllerTest {

    @Mock
    private ShoppingCartService shoppingCartService;

    @InjectMocks
    private ShoppingCartController shoppingCartController;

    private ShoppingCart testCart;

    /**
     * Initializes a reusable test cart instance before each test.
     */
    @BeforeEach
    void setUp() {
        testCart = new ShoppingCart();
        testCart.setId("cart1");
        testCart.setUserId("user1");
        testCart.setItems(new ArrayList<>());
    }

    /**
     * Verifies that GET /api/cart/{userId}
     * returns the cart for the specified user.
     */
    @Test
    void getCart_ShouldReturnCartForUser() {
        // Mock the service layer to return our prepared test cart
        when(shoppingCartService.getCartByUserId("user1")).thenReturn(testCart);

        // Call the controller method
        ResponseEntity<ShoppingCart> response = shoppingCartController.getCart("user1");

        // Validate the HTTP response
        // Should return 200 OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Body should not be null
        assertNotNull(response.getBody());
        // Returned cart should match the user
        assertEquals("user1", response.getBody().getUserId());

        // Ensure the service layer method was called exactly once
        verify(shoppingCartService, times(1)).getCartByUserId("user1");
    }

    /**
     * Verifies that POST /api/cart/{userId}/items
     * delegates correctly to the service and returns the updated cart.
     */
    @Test
    void addItemToCart_ShouldReturnUpdatedCart() {
        // Create a simulated request payload
        AddToCartRequest request = new AddToCartRequest("book1", 2);

        // Mock service behavior
        when(shoppingCartService.addItemToCart("user1", "book1", 2)).thenReturn(testCart);

        // Trigger controller logic
        ResponseEntity<ShoppingCart> response = shoppingCartController.addItemToCart("user1", request);

        // Validate response, Should return 200 OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Should return an updated cart
        assertNotNull(response.getBody());

        // Verify correct service interaction
        verify(shoppingCartService, times(1))
                .addItemToCart("user1", "book1", 2);
    }

    /**
     * Verifies that DELETE /api/cart/{userId}/items/{bookId}
     * removes the item and returns the updated cart.
     */
    @Test
    void removeItemFromCart_ShouldReturnUpdatedCart() {
        // Mock service behavior to return the modified cart
        when(shoppingCartService.updateItemQuantity("user1", "book1", 3))
                .thenReturn(testCart);

        // Call controller method
        ResponseEntity<ShoppingCart> response =
                shoppingCartController.updateItemQuantity("user1", "book1", 3);

        // Validate result
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        // Verify interaction with service
        verify(shoppingCartService, times(1))
                .updateItemQuantity("user1", "book1", 3);
    }

    /**
     * Verifies that DELETE /api/cart/{userId}
     * clears all items and returns 204 No Content.
     */
    @Test
    void clearCart_ShouldReturnNoContent() {
        // Mock service layer
        doNothing().when(shoppingCartService).clearCart("user1");

        // Execute controller method
        ResponseEntity<Void> response = shoppingCartController.clearCart("user1");

        // Validate HTTP status, 204 No Content expected
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        // No body expected
        assertNull(response.getBody());

        // Ensure clearCart was invoked exactly once
        verify(shoppingCartService, times(1)).clearCart("user1");
    }
}
