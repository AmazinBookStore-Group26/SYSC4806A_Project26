package org.amazinbookstore.controller;

import org.amazinbookstore.model.Order;
import org.amazinbookstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

/**
 * REST controller for managing customer orders.
 */
public class OrderController {

    private final OrderService orderService;

    /**
     * Creates a new order for the specified user by converting the user's cart into an order.
     *
     * @param userId the ID of the user checking out
     * @return the created {@link Order} with HTTP 201 (Created)
     */
    @PostMapping("/checkout/{userId}")
    public ResponseEntity<Order> checkout(@PathVariable String userId) {
        Order order = orderService.createOrderFromCart(userId);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    /**
     * Retrieves a single order by its ID.
     *
     * @param orderId the ID of the order
     * @return the order if found, with HTTP 200 (OK)
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Retrieves all orders belonging to a specific user.
     *
     * @param userId the ID of the user
     * @return list of orders for the user with HTTP 200 (OK)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable String userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Retrieves all orders in the system.
     * Typically used by administrators to monitor purchase activity.
     *
     * @return list of all orders with HTTP 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Updates the status of an existing order.
     *
     * @param orderId the ID of the order to update
     * @param status the new {@link Order.OrderStatus} to apply
     * @return the updated order with HTTP 200 (OK)
     */
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam Order.OrderStatus status
    ) {
        Order order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(order);
    }
}