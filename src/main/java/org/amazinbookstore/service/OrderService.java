package org.amazinbookstore.service;

import org.amazinbookstore.exception.InsufficientInventoryException;
import org.amazinbookstore.exception.ResourceNotFoundException;
import org.amazinbookstore.model.*;
import org.amazinbookstore.repository.OrderRepository;
import org.amazinbookstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ShoppingCartService shoppingCartService;
    private final BookService bookService;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                       ShoppingCartService shoppingCartService,
                       BookService bookService,
                       UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.shoppingCartService = shoppingCartService;
        this.bookService = bookService;
        this.userRepository = userRepository;
    }

    /**
     * Create order from user's cart (Checkout Simulation)
     * - Validates stock availability
     * - Deducts inventory
     * - Records purchase history for recommendations
     */
    @Transactional
    public Order createOrderFromCart(String userId) {
        // Get user's cart
        ShoppingCart cart = shoppingCartService.getCartByUserId(userId);

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create order from empty cart");
        }

        // STEP 1: Validate stock for all items
        for (CartItem cartItem : cart.getItems()) {
            Book book = bookService.getBookById(cartItem.getBookId());
            if (book == null) {
                throw new ResourceNotFoundException("Book not found with id: " + cartItem.getBookId());
            }
            if (book.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientInventoryException(
                        "Insufficient inventory for book: " + book.getTitle() +
                                ". Available: " + book.getStockQuantity() +
                                ", Requested: " + cartItem.getQuantity()
                );
            }
        }

        // STEP 2: Create order
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.CONFIRMED);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

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
            BigDecimal itemTotal = book.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            // STEP 3: Decrease inventory
            bookService.decreaseInventory(book.getId(), cartItem.getQuantity());
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // STEP 4: Update user's purchase history (for recommendations)
        updateUserPurchaseHistory(userId, orderItems);

        // Clear the cart
        shoppingCartService.clearCart(userId);

        return savedOrder;
    }

    /**
     * Update user's purchase history for book recommendations
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

    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrderStatus(String orderId, Order.OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
