package org.amazinbookstore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "books")
public class Book {

    @Id
    private String id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "Publisher is required")
    private String publisher;

    @NotBlank(message = "ISBN is required")
    private String isbn;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private String genre;

    private Integer publicationYear;

    private String description;

    @Min(value = 0, message = "Inventory cannot be negative")
    private Integer inventory;

    private String pictureUrl;

    // Custom constructor for tests and basic book creation
    public Book(String title, String author, String publisher, String isbn, BigDecimal price) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
        this.price = price;
    }


    public @Min(value = 0, message = "Stock quantity cannot be negative") Integer getStockQuantity() {
        return stockQuantity;
    }

    public @NotNull(message = "Price is required") @Positive(message = "Price must be positive") BigDecimal getPrice() {
        return price;
    }
}
