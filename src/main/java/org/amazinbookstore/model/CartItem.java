package org.amazinbookstore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    // The identifier of the book associated with this cart item.
    // Must be a non-empty string.
    @NotBlank(message = "Book ID is required")
    private String bookId;

    // The quantity of the selected book within the cart.
    // Must be at least 1.
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
