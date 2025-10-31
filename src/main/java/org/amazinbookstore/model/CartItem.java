package org.amazinbookstore.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItem {

    @NotBlank(message = "Book ID is required")
    private String bookId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    public CartItem() {
    }

    public CartItem(String bookId, Integer quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

}
