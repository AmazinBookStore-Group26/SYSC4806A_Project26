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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    private Book book1;
    private Book book2;
    private ShoppingCart cart;
    private User user;
    private Order order;

    @BeforeEach
    void setUp() {
        // Create test books with some inventory
        book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "Scribner", "978-0743273565", new BigDecimal("15.99"));
        book1.setId("book1");
        book1.setInventory(10);

        book2 = new Book("1984", "George Orwell", "Signet Classic", "978-0451524935", new BigDecimal("12.99"));
        book2.setId("book2");
        book2.setInventory(5);

        // Set up a cart with 2 items
        cart = new ShoppingCart();
        cart.setUserId("user1");
        CartItem item1 = new CartItem();
        item1.setBookId("book1");
        item1.setQuantity(2);
        CartItem item2 = new CartItem();
        item2.setBookId("book2");
        item2.setQuantity(1);
        cart.setItems(Arrays.asList(item1, item2));

        // Create test user with empty purchase history
        user = new User();
        user.setId("user1");
        user.setUsername("testuser");
        user.setPurchasedBookIds(new ArrayList<>());

        // Create a sample order
        order = new Order();
        order.setId("order1");
        order.setUserId("user1");
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.CONFIRMED);
        order.setTotalAmount(44.97);
    }

    @Test
    void testCreateOrderFromCart_Success() {
        when(shoppingCartService.getCartByUserId("user1")).thenReturn(cart);
        when(bookService.getBookById("book1")).thenReturn(book1);
        when(bookService.getBookById("book2")).thenReturn(book2);
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(bookService).decreaseInventory(anyString(), anyInt());
        doNothing().when(shoppingCartService).clearCart("user1");
        when(userRepository.save(any(User.class))).thenReturn(user);

        Order result = orderService.createOrderFromCart("user1");

        assertNotNull(result);
        assertEquals("order1", result.getId());
        assertEquals("user1", result.getUserId());
        assertEquals(Order.OrderStatus.CONFIRMED, result.getStatus());

        // Make sure inventory was decreased for both books
        verify(bookService).decreaseInventory("book1", 2);
        verify(bookService).decreaseInventory("book2", 1);

        // Cart should be cleared after order creation
        verify(shoppingCartService).clearCart("user1");

        // User purchase history should be updated
        verify(userRepository).save(user);
    }

    @Test
    void testCreateOrderFromCart_EmptyCart() {
        ShoppingCart emptyCart = new ShoppingCart();
        emptyCart.setUserId("user1");
        emptyCart.setItems(new ArrayList<>());
        when(shoppingCartService.getCartByUserId("user1")).thenReturn(emptyCart);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> orderService.createOrderFromCart("user1"));

        assertEquals("Cannot create order from empty cart", exception.getMessage());

        // Shouldn't try to save an order from empty cart
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCreateOrderFromCart_InsufficientInventory() {
        // Set book1 to have only 1 in stock, but cart wants 2
        book1.setInventory(1);
        when(shoppingCartService.getCartByUserId("user1")).thenReturn(cart);
        when(bookService.getBookById("book1")).thenReturn(book1);

        InsufficientInventoryException exception = assertThrows(InsufficientInventoryException.class,
            () -> orderService.createOrderFromCart("user1"));

        assertTrue(exception.getMessage().contains("Insufficient inventory"));
        assertTrue(exception.getMessage().contains("The Great Gatsby"));

        // Order shouldn't be saved if inventory check fails
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCreateOrderFromCart_CalculatesTotalCorrectly() {
        when(shoppingCartService.getCartByUserId("user1")).thenReturn(cart);
        when(bookService.getBookById("book1")).thenReturn(book1);
        when(bookService.getBookById("book2")).thenReturn(book2);
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(bookService).decreaseInventory(anyString(), anyInt());
        doNothing().when(shoppingCartService).clearCart("user1");

        // Capture the order that gets saved to check the total
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId("order1");
            return savedOrder;
        });

        Order result = orderService.createOrderFromCart("user1");

        assertNotNull(result);
        // Total: (15.99 * 2) + (12.99 * 1) = 31.98 + 12.99 = 44.97
        assertEquals(44.97, result.getTotalAmount(), 0.01);
    }

    @Test
    void testCreateOrderFromCart_UpdatesUserPurchaseHistory() {
        when(shoppingCartService.getCartByUserId("user1")).thenReturn(cart);
        when(bookService.getBookById("book1")).thenReturn(book1);
        when(bookService.getBookById("book2")).thenReturn(book2);
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(bookService).decreaseInventory(anyString(), anyInt());
        doNothing().when(shoppingCartService).clearCart("user1");

        orderService.createOrderFromCart("user1");

        // User should now have both books in their purchase history
        verify(userRepository).save(argThat(u ->
            u.getPurchasedBookIds().contains("book1") &&
            u.getPurchasedBookIds().contains("book2")
        ));
    }

    @Test
    void testCreateOrderFromCart_UserNotFound() {
        when(shoppingCartService.getCartByUserId("user1")).thenReturn(cart);
        when(bookService.getBookById("book1")).thenReturn(book1);
        when(bookService.getBookById("book2")).thenReturn(book2);
        when(userRepository.findById("user1")).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(bookService).decreaseInventory(anyString(), anyInt());

        // Should fail when trying to update purchase history
        assertThrows(ResourceNotFoundException.class,
            () -> orderService.createOrderFromCart("user1"));
    }

    @Test
    void testGetOrderById_Found() {
        when(orderRepository.findById("order1")).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById("order1");

        assertNotNull(result);
        assertEquals("order1", result.getId());
        assertEquals("user1", result.getUserId());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById("order999")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> orderService.getOrderById("order999"));

        assertTrue(exception.getMessage().contains("Order not found"));
    }

    @Test
    void testGetOrdersByUserId() {
        Order order2 = new Order();
        order2.setId("order2");
        order2.setUserId("user1");

        List<Order> orders = Arrays.asList(order, order2);
        when(orderRepository.findByUserIdOrderByOrderDateDesc("user1")).thenReturn(orders);

        List<Order> result = orderService.getOrdersByUserId("user1");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("order1", result.get(0).getId());
        assertEquals("order2", result.get(1).getId());
    }

    @Test
    void testGetOrdersByUserId_EmptyList() {
        when(orderRepository.findByUserIdOrderByOrderDateDesc("user999")).thenReturn(new ArrayList<>());

        List<Order> result = orderService.getOrdersByUserId("user999");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllOrders() {
        Order order2 = new Order();
        order2.setId("order2");
        List<Order> orders = Arrays.asList(order, order2);
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testUpdateOrderStatus_Success() {
        when(orderRepository.findById("order1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.updateOrderStatus("order1", Order.OrderStatus.COMPLETED);

        assertNotNull(result);
        assertEquals(Order.OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void testUpdateOrderStatus_OrderNotFound() {
        when(orderRepository.findById("order999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> orderService.updateOrderStatus("order999", Order.OrderStatus.CANCELLED));

        // Shouldn't try to save if order doesn't exist
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testUpdateOrderStatus_AllStatuses() {
        when(orderRepository.findById("order1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Try updating to each possible status
        Order.OrderStatus[] statuses = {
            Order.OrderStatus.PENDING,
            Order.OrderStatus.CONFIRMED,
            Order.OrderStatus.COMPLETED,
            Order.OrderStatus.CANCELLED
        };

        for (Order.OrderStatus status : statuses) {
            Order result = orderService.updateOrderStatus("order1", status);
            assertEquals(status, result.getStatus());
        }

        // Should have saved once per status change
        verify(orderRepository, times(4)).save(order);
    }
}
