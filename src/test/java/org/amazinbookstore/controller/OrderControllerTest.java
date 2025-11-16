package org.amazinbookstore.controller;

import org.amazinbookstore.exception.InsufficientInventoryException;
import org.amazinbookstore.exception.ResourceNotFoundException;
import org.amazinbookstore.model.Order;
import org.amazinbookstore.model.OrderItem;
import org.amazinbookstore.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    private OrderService orderService;
    private OrderController orderController;
    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        orderController = new OrderController(orderService);

        // Create an order with 2 items
        order1 = new Order();
        order1.setId("order1");
        order1.setUserId("user1");
        order1.setOrderDate(LocalDateTime.now().minusDays(2));
        order1.setStatus(Order.OrderStatus.CONFIRMED);
        order1.setTotalAmount(44.97);

        OrderItem item1 = new OrderItem();
        item1.setBookId("book1");
        item1.setBookTitle("The Great Gatsby");
        item1.setQuantity(2);
        item1.setPriceAtPurchase(new BigDecimal("15.99"));

        OrderItem item2 = new OrderItem();
        item2.setBookId("book2");
        item2.setBookTitle("1984");
        item2.setQuantity(1);
        item2.setPriceAtPurchase(new BigDecimal("12.99"));

        order1.setItems(Arrays.asList(item1, item2));

        // Create a completed order
        order2 = new Order();
        order2.setId("order2");
        order2.setUserId("user1");
        order2.setOrderDate(LocalDateTime.now().minusDays(1));
        order2.setStatus(Order.OrderStatus.COMPLETED);
        order2.setTotalAmount(18.99);

        OrderItem item3 = new OrderItem();
        item3.setBookId("book3");
        item3.setBookTitle("To Kill a Mockingbird");
        item3.setQuantity(1);
        item3.setPriceAtPurchase(new BigDecimal("18.99"));

        order2.setItems(Arrays.asList(item3));
    }

    @Test
    void testCheckout_Success() {
        when(orderService.createOrderFromCart("user1")).thenReturn(order1);

        ResponseEntity<Order> response = orderController.checkout("user1");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("order1", response.getBody().getId());
        assertEquals("user1", response.getBody().getUserId());
        assertEquals(44.97, response.getBody().getTotalAmount());
        assertEquals(Order.OrderStatus.CONFIRMED, response.getBody().getStatus());
        assertEquals(2, response.getBody().getItems().size());
    }

    @Test
    void testCheckout_EmptyCart() {
        when(orderService.createOrderFromCart("user1"))
            .thenThrow(new IllegalStateException("Cannot create order from empty cart"));

        assertThrows(IllegalStateException.class, () -> orderController.checkout("user1"));
    }

    @Test
    void testCheckout_InsufficientInventory() {
        when(orderService.createOrderFromCart("user1"))
            .thenThrow(new InsufficientInventoryException("Insufficient inventory for book: The Great Gatsby"));

        InsufficientInventoryException exception = assertThrows(InsufficientInventoryException.class,
            () -> orderController.checkout("user1"));

        assertTrue(exception.getMessage().contains("Insufficient inventory"));
    }

    @Test
    void testGetOrderById_Found() {
        when(orderService.getOrderById("order1")).thenReturn(order1);

        ResponseEntity<Order> response = orderController.getOrderById("order1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("order1", response.getBody().getId());
        assertEquals("user1", response.getBody().getUserId());
        assertEquals(2, response.getBody().getItems().size());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderService.getOrderById("order999"))
            .thenThrow(new ResourceNotFoundException("Order not found with id: order999"));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> orderController.getOrderById("order999"));

        assertTrue(exception.getMessage().contains("Order not found"));
    }

    @Test
    void testGetUserOrders_Success() {
        List<Order> orders = Arrays.asList(order2, order1); // Most recent first
        when(orderService.getOrdersByUserId("user1")).thenReturn(orders);

        ResponseEntity<List<Order>> response = orderController.getUserOrders("user1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        // order2 should be first since it's more recent
        assertEquals("order2", response.getBody().get(0).getId());
        assertEquals("order1", response.getBody().get(1).getId());
    }

    @Test
    void testGetUserOrders_EmptyList() {
        when(orderService.getOrdersByUserId("user999")).thenReturn(new ArrayList<>());

        ResponseEntity<List<Order>> response = orderController.getUserOrders("user999");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetUserOrders_OrderedByDateDesc() {
        List<Order> orders = Arrays.asList(order2, order1);
        when(orderService.getOrdersByUserId("user1")).thenReturn(orders);

        ResponseEntity<List<Order>> response = orderController.getUserOrders("user1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        // Most recent order should be first
        assertTrue(response.getBody().get(0).getOrderDate()
            .isAfter(response.getBody().get(1).getOrderDate()));
    }

    @Test
    void testGetAllOrders_Success() {
        Order order3 = new Order();
        order3.setId("order3");
        order3.setUserId("user2");

        List<Order> orders = Arrays.asList(order1, order2, order3);
        when(orderService.getAllOrders()).thenReturn(orders);

        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
    }

    @Test
    void testGetAllOrders_EmptyList() {
        when(orderService.getAllOrders()).thenReturn(new ArrayList<>());

        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testUpdateOrderStatus_ToPending() {
        order1.setStatus(Order.OrderStatus.PENDING);
        when(orderService.updateOrderStatus("order1", Order.OrderStatus.PENDING)).thenReturn(order1);

        ResponseEntity<Order> response = orderController.updateOrderStatus("order1", Order.OrderStatus.PENDING);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Order.OrderStatus.PENDING, response.getBody().getStatus());
    }

    @Test
    void testUpdateOrderStatus_ToConfirmed() {
        when(orderService.updateOrderStatus("order1", Order.OrderStatus.CONFIRMED)).thenReturn(order1);

        ResponseEntity<Order> response = orderController.updateOrderStatus("order1", Order.OrderStatus.CONFIRMED);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Order.OrderStatus.CONFIRMED, response.getBody().getStatus());
    }

    @Test
    void testUpdateOrderStatus_ToCompleted() {
        order1.setStatus(Order.OrderStatus.COMPLETED);
        when(orderService.updateOrderStatus("order1", Order.OrderStatus.COMPLETED)).thenReturn(order1);

        ResponseEntity<Order> response = orderController.updateOrderStatus("order1", Order.OrderStatus.COMPLETED);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Order.OrderStatus.COMPLETED, response.getBody().getStatus());
    }

    @Test
    void testUpdateOrderStatus_ToCancelled() {
        order1.setStatus(Order.OrderStatus.CANCELLED);
        when(orderService.updateOrderStatus("order1", Order.OrderStatus.CANCELLED)).thenReturn(order1);

        ResponseEntity<Order> response = orderController.updateOrderStatus("order1", Order.OrderStatus.CANCELLED);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Order.OrderStatus.CANCELLED, response.getBody().getStatus());
    }

    @Test
    void testUpdateOrderStatus_OrderNotFound() {
        when(orderService.updateOrderStatus("order999", Order.OrderStatus.COMPLETED))
            .thenThrow(new ResourceNotFoundException("Order not found with id: order999"));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> orderController.updateOrderStatus("order999", Order.OrderStatus.COMPLETED));

        assertTrue(exception.getMessage().contains("Order not found"));
    }

    @Test
    void testCheckout_VerifiesOrderDetails() {
        when(orderService.createOrderFromCart("user1")).thenReturn(order1);

        ResponseEntity<Order> response = orderController.checkout("user1");

        Order responseOrder = response.getBody();
        assertNotNull(responseOrder);
        assertEquals(2, responseOrder.getItems().size());

        // Check first item details
        OrderItem firstItem = responseOrder.getItems().get(0);
        assertEquals("book1", firstItem.getBookId());
        assertEquals("The Great Gatsby", firstItem.getBookTitle());
        assertEquals(2, firstItem.getQuantity());
        assertEquals(new BigDecimal("15.99"), firstItem.getPriceAtPurchase());

        // Check second item details
        OrderItem secondItem = responseOrder.getItems().get(1);
        assertEquals("book2", secondItem.getBookId());
        assertEquals("1984", secondItem.getBookTitle());
        assertEquals(1, secondItem.getQuantity());
        assertEquals(new BigDecimal("12.99"), secondItem.getPriceAtPurchase());
    }
}
