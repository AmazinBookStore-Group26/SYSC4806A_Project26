package org.amazinbookstore.service;

import org.amazinbookstore.exception.InsufficientInventoryException;
import org.amazinbookstore.exception.ResourceNotFoundException;
import org.amazinbookstore.model.*;
import org.amazinbookstore.repository.OrderRepository;
import org.amazinbookstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ShoppingCartService shoppingCartService;

    @Mock
    private BookService bookService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Book book1;
    private Book book2;
    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user1");
        user.setUsername("testuser");
        user.setPurchasedBookIds(new ArrayList<>());

        book1 = new Book();
        book1.setId("book1");
        book1.setTitle("Test Book 1");
        book1.setPrice(new BigDecimal("19.99"));
        book1.setStockQuantity(10);

        book2 = new Book();
        book2.setId("book2");
        book2.setTitle("Test Book 2");
        book2.setPrice(new BigDecimal("29.99"));
        book2.setStockQuantity(5);

        cart = new ShoppingCart();
        cart.setUserId("user1");
        cart.setItems(new ArrayList<>());
    }

    @Test
    void testCreateOrderFromCart_Success() {
        // Setup cart with items
        cart.addItem("book1", 2);
        cart.addItem("book2", 1);

        when(shoppingCartService.getCartByUserId("user1")).thenReturn(cart);
        when(bookService.getBookById("book1")).thenReturn(book1);
        when(bookService.getBookById("book2")).thenReturn(book2);
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId("order1");
            return order;
        });

        Order result = orderService.createOrderFromCart("user1");

        assertNotNull(result);
        assertEquals("user1", result.getUserId());
        assertEquals(2, result.getItems().size());
        assertEquals(new BigDecimal("69.97"), result.getTotalAmount());
        assertEquals(Order.OrderStatus.CONFIRMED, result.getStatus());

        // Verify inventory was decreased
        verify(bookService, times(1)).decreaseInventory("book1", 2);
        verify(bookService, times(1)).decreaseInventory("book2", 1);

        // Verify cart was cleared
        verify(shoppingCartService, times(1)).clearCart("user1");

        // Verify user purchase history was updated
        verify(userRepository, times(1)).save(user);
        assertTrue(user.getPurchasedBookIds().contains("book1"));
        assertTrue(user.getPurchasedBookIds().contains("book2"));
    }

    @Test
    void testCreateOrderFromCart_EmptyCart() {
        when(shoppingCartService.getCartByUserId("user1")).thenReturn(cart);

        assertThrows(IllegalStateException.class, () -> {
            orderService.createOrderFromCart("user1");
        });
    }

    @Test
    void testCreateOrderFromCart_InsufficientInventory() {
        cart.addItem("book1", 20); // Request more than available

        when(shoppingCartService.getCartByUserId("user1")).thenReturn(cart);
        when(bookService.getBookById("book1")).thenReturn(book1);

        InsufficientInventoryException exception = assertThrows(InsufficientInventoryException.class, () -> {
            orderService.createOrderFromCart("user1");
        });

        assertTrue(exception.getMessage().contains("Insufficient inventory"));
        assertTrue(exception.getMessage().contains("Available: 10"));
        assertTrue(exception.getMessage().contains("Requested: 20"));

        // Verify inventory was NOT decreased
        verify(bookService, never()).decreaseInventory(anyString(), anyInt());

        // Verify cart was NOT cleared
        verify(shoppingCartService, never()).clearCart("user1");
    }

    @Test
    void testCreateOrderFromCart_BookNotFound() {
        cart.addItem("invalid-book", 1);

        when(shoppingCartService.getCartByUserId("user1")).thenReturn(cart);
        when(bookService.getBookById("invalid-book")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.createOrderFromCart("user1");
        });

        verify(bookService, never()).decreaseInventory(anyString(), anyInt());
        verify(shoppingCartService, never()).clearCart("user1");
    }

    @Test
    void testGetOrderById_Found() {
        Order order = new Order();
        order.setId("order1");
        order.setUserId("user1");

        when(orderRepository.findById("order1")).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById("order1");

        assertNotNull(result);
        assertEquals("order1", result.getId());
        verify(orderRepository, times(1)).findById("order1");
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.getOrderById("invalid");
        });
    }

    @Test
    void testGetOrdersByUserId() {
        Order order1 = new Order();
        order1.setId("order1");
        Order order2 = new Order();
        order2.setId("order2");

        when(orderRepository.findByUserIdOrderByOrderDateDesc("user1"))
                .thenReturn(Arrays.asList(order1, order2));

        var result = orderService.getOrdersByUserId("user1");

        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findByUserIdOrderByOrderDateDesc("user1");
    }

    @Test
    void testUpdateOrderStatus() {
        Order order = new Order();
        order.setId("order1");
        order.setStatus(Order.OrderStatus.PENDING);

        when(orderRepository.findById("order1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.updateOrderStatus("order1", Order.OrderStatus.COMPLETED);

        assertEquals(Order.OrderStatus.COMPLETED, result.getStatus());
        verify(orderRepository, times(1)).save(order);
    }
}
