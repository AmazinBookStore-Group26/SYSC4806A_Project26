package org.amazinbookstore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private String bookId;
    private String bookTitle;
    private Integer quantity;
    private Double priceAtPurchase;
}
