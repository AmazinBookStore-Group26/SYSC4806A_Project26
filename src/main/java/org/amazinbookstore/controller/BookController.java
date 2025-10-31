package org.amazinbookstore.controller;

import org.amazinbookstore.model.Book;
import org.amazinbookstore.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * GET /books - Search, filter, and sort books
     * Query parameters:
     *   - author: Filter by author name (case-insensitive, partial match)
     *   - publisher: Filter by publisher name (case-insensitive, partial match)
     *   - genre: Filter by genre (case-insensitive, partial match)
     *   - title: Filter by title (case-insensitive, partial match)
     *   - sort: Sort results (price, price_desc, title, author, year, year_desc)
     *
     * Examples:
     *   GET /books?author=Rowling
     *   GET /books?publisher=Penguin&sort=price
     *   GET /books?genre=Fiction&sort=title
     */
    @GetMapping
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String sort) {

        List<Book> books = bookService.searchBooks(author, publisher, genre, title, sort);
        return ResponseEntity.ok(books);
    }

    /**
     * GET /books/{id} - Get a specific book by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable String id) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }

    /**
     * POST /books - Create a new book
     */
    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        Book savedBook = bookService.saveBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    /**
     * PUT /books/{id} - Update an existing book
     */
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable String id, @Valid @RequestBody Book book) {
        Book existingBook = bookService.getBookById(id);
        if (existingBook == null) {
            return ResponseEntity.notFound().build();
        }
        book.setId(id);
        Book updatedBook = bookService.saveBook(book);
        return ResponseEntity.ok(updatedBook);
    }

    /**
     * DELETE /books/{id} - Delete a book
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        Book existingBook = bookService.getBookById(id);
        if (existingBook == null) {
            return ResponseEntity.notFound().build();
        }
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
