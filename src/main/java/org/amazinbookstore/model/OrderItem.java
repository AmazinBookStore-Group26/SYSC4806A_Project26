package org.amazinbookstore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private String bookId;
    private String bookTitle;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
}
