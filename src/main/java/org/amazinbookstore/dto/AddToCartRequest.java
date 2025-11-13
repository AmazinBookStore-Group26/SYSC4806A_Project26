package org.amazinbookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartRequest {

    // The identifier of the book to add to the user's cart.
    // Must not be null, empty, or whitespace.
    @NotBlank(message = "Book ID is required")
    private String bookId;

    //The number of copies of the book to add.
    // Quantity must be at least 1.
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
