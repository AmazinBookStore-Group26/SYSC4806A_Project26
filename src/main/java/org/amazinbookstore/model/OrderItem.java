package org.amazinbookstore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents a single line item within an {@link Order}.
 *
 * Each item stores the book ID, title, purchased quantity,
 * and the price at the time the order was created.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private String bookId;
    private String bookTitle;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
}
