package org.amazinbookstore.service;

import org.amazinbookstore.exception.InsufficientInventoryException;
import org.amazinbookstore.exception.ResourceNotFoundException;
import org.amazinbookstore.model.*;
import org.amazinbookstore.repository.OrderRepository;
import org.amazinbookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for order creation, retrieval, and updates.
 *
 * Handles:
 *
 *     Converting a user's shopping cart into a confirmed order
 *     Validating inventory before order creation
 *     Updating book inventory counts
 *     Maintaining user purchase history
 *     Basic CRUD operations on orders
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ShoppingCartService shoppingCartService;
    private final BookService bookService;
    private final UserRepository userRepository;

    /**
     * Creates an order based on the contents of a user's shopping cart.
     *
     * @param userId the ID of the user checking out
     * @return the saved {@link Order}
     * @throws IllegalStateException if the cart is empty
     * @throws InsufficientInventoryException if any requested quantity exceeds inventory
     */
    @Transactional
    public Order createOrderFromCart(String userId) {
        // Get user's cart
        ShoppingCart cart = shoppingCartService.getCartByUserId(userId);

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create order from empty cart");
        }

        // Validate inventory for all items
        for (CartItem cartItem : cart.getItems()) {
            Book book = bookService.getBookById(cartItem.getBookId());
            if (book.getInventory() < cartItem.getQuantity()) {
                throw new InsufficientInventoryException(
                        "Insufficient inventory for book: " + book.getTitle() +
                                ". Available: " + book.getInventory() +
                                ", Requested: " + cartItem.getQuantity()
                );
            }
        }

        // Create order
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.CONFIRMED);

        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;

        // Process each cart item
        for (CartItem cartItem : cart.getItems()) {
            Book book = bookService.getBookById(cartItem.getBookId());

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setBookId(book.getId());
            orderItem.setBookTitle(book.getTitle());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(book.getPrice());
            orderItems.add(orderItem);

            // Calculate total
            totalAmount += book.getPrice().doubleValue() * cartItem.getQuantity();

            // Decrease inventory
            bookService.decreaseInventory(book.getId(), cartItem.getQuantity());
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Update user's purchase history
        updateUserPurchaseHistory(userId, orderItems);

        // Clear the cart
        shoppingCartService.clearCart(userId);

        return savedOrder;
    }

    /**
     * Updates the list of purchased book IDs for a user after an order is completed.
     *
     * @param userId     the user who placed the order
     * @param orderItems the items included in the order
     * @throws ResourceNotFoundException if the user does not exist
     */
    private void updateUserPurchaseHistory(String userId, List<OrderItem> orderItems) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        for (OrderItem item : orderItems) {
            if (!user.getPurchasedBookIds().contains(item.getBookId())) {
                user.getPurchasedBookIds().add(item.getBookId());
            }
        }

        userRepository.save(user);
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId the ID of the order
     * @return the matching {@link Order}
     * @throws ResourceNotFoundException if the order cannot be found
     */
    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    /**
     * Retrieves all orders placed by a specific user, sorted by most recent first.
     *
     * @param userId the ID of the user
     * @return list of the user's orders
     */
    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    /**
     * Retrieves every order in the system.
     *
     * @return list of all orders
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Updates the status of an existing order.
     *
     * @param orderId the ID of the order
     * @param status  the new status to apply
     * @return the updated order
     */
    public Order updateOrderStatus(String orderId, Order.OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}