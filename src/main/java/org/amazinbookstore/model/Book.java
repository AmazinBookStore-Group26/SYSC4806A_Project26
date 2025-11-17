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

/**
 * Represents a book in the Amazin Bookstore catalog.
 *
 * This entity is stored in the MongoDB books collection and contains
 * basic metadata such as title, author, pricing, inventory, and optional fields like
 * genre, publication year, and picture URL.
 */
public class Book {

    @Id
    private String id;

    /**
     * Title of the book.
     * Must not be blank.
     */
    @NotBlank(message = "Title is required")
    private String title;

    /**
     * Author of the book.
     * Must not be blank.
     */
    @NotBlank(message = "Author is required")
    private String author;

    /**
     * Publisher of the book.
     * Must not be blank.
     */
    @NotBlank(message = "Publisher is required")
    private String publisher;

    /**
     * ISBN identifier for the book.
     * Must not be blank.
     */
    @NotBlank(message = "ISBN is required")
    private String isbn;

    /**
     * Price of the book.
     * Must be a positive decimal value.
     */
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    /**
     * Optional genre classification for the book.
     */
    private String genre;

    /**
     * Year the book was published.
     * Optional field.
     */
    private Integer publicationYear;

    /**
     * Human-readable description of the book.
     * Optional.
     */
    private String description;

    /**
     * Number of copies available in inventory.
     * Must be zero or greater.
     */
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

}
