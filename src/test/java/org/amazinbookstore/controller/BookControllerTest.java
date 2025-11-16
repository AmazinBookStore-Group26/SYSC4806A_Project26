package org.amazinbookstore.controller;

import org.amazinbookstore.model.Book;
import org.amazinbookstore.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookController.
 * Tests all REST API endpoints for book management operations.
 */
class BookControllerTest {

    private BookService bookService;
    private BookController bookController;
    private Book book1;
    private Book book2;

    /**
     * Sets up test data and mocks before each test.
     * Initializes two sample books and mocked BookService.
     */
    @BeforeEach
    void setUp() {
        bookService = mock(BookService.class);
        bookController = new BookController(bookService);

        book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "Scribner", "978-0743273565", new BigDecimal("15.99"));
        book1.setId("1");

        book2 = new Book("1984", "George Orwell", "Signet Classic", "978-0451524935", new BigDecimal("12.99"));
        book2.setId("2");
    }

    /**
     * Tests searching for books without any filters.
     * Should return all books and verify service is called correctly.
     */
    @Test
    void testSearchBooks_NoFilters() {
        when(bookService.searchBooks(null, null, null, null, null))
                .thenReturn(Arrays.asList(book1, book2));

        ResponseEntity<List<Book>> response = bookController.searchBooks(null, null, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(bookService, times(1)).searchBooks(null, null, null, null, null);
    }

    /**
     * Tests searching for books with author filter.
     * Should return only books matching the specified author.
     */
    @Test
    void testSearchBooks_WithAuthorFilter() {
        when(bookService.searchBooks("Orwell", null, null, null, null))
                .thenReturn(Arrays.asList(book2));

        ResponseEntity<List<Book>> response = bookController.searchBooks("Orwell", null, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("1984", response.getBody().get(0).getTitle());
    }

    /**
     * Tests searching for books with price sorting.
     * Should return books sorted by price in ascending order.
     */
    @Test
    void testSearchBooks_WithSort() {
        when(bookService.searchBooks(null, null, null, null, "price"))
                .thenReturn(Arrays.asList(book2, book1));

        ResponseEntity<List<Book>> response = bookController.searchBooks(null, null, null, null, "price");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().get(0).getPrice().compareTo(response.getBody().get(1).getPrice()) <= 0);
    }

    /**
     * Tests retrieving a book by ID when the book exists.
     * Should return OK status and the correct book.
     */
    @Test
    void testGetBookById_Found() {
        when(bookService.getBookById("1")).thenReturn(book1);

        ResponseEntity<Book> response = bookController.getBookById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The Great Gatsby", response.getBody().getTitle());
        verify(bookService, times(1)).getBookById("1");
    }

    /**
     * Tests retrieving a book by ID when the book does not exist.
     * Should return NOT_FOUND status.
     */
    @Test
    void testGetBookById_NotFound() {
        when(bookService.getBookById("999")).thenReturn(null);

        ResponseEntity<Book> response = bookController.getBookById("999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(bookService, times(1)).getBookById("999");
    }

    /**
     * Tests creating a new book.
     * Should return CREATED status and the saved book.
     */
    @Test
    void testCreateBook() {
        when(bookService.saveBook(any(Book.class))).thenReturn(book1);

        ResponseEntity<Book> response = bookController.createBook(book1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The Great Gatsby", response.getBody().getTitle());
        verify(bookService, times(1)).saveBook(any(Book.class));
    }

    /**
     * Tests updating an existing book successfully.
     * Should return OK status and the updated book.
     */
    @Test
    void testUpdateBook_Success() {
        when(bookService.getBookById("1")).thenReturn(book1);
        when(bookService.saveBook(any(Book.class))).thenReturn(book1);

        ResponseEntity<Book> response = bookController.updateBook("1", book1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(bookService, times(1)).getBookById("1");
        verify(bookService, times(1)).saveBook(any(Book.class));
    }

    /**
     * Tests updating a book that does not exist.
     * Should return NOT_FOUND status and not save the book.
     */
    @Test
    void testUpdateBook_NotFound() {
        when(bookService.getBookById("999")).thenReturn(null);

        ResponseEntity<Book> response = bookController.updateBook("999", book1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(bookService, times(1)).getBookById("999");
        verify(bookService, times(0)).saveBook(any(Book.class));
    }

    /**
     * Tests deleting an existing book successfully.
     * Should return NO_CONTENT status and delete the book.
     */
    @Test
    void testDeleteBook_Success() {
        when(bookService.getBookById("1")).thenReturn(book1);
        doNothing().when(bookService).deleteBook("1");

        ResponseEntity<Void> response = bookController.deleteBook("1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookService, times(1)).getBookById("1");
        verify(bookService, times(1)).deleteBook("1");
    }

    /**
     * Tests deleting a book that does not exist.
     * Should return NOT_FOUND status and not attempt deletion.
     */
    @Test
    void testDeleteBook_NotFound() {
        when(bookService.getBookById("999")).thenReturn(null);

        ResponseEntity<Void> response = bookController.deleteBook("999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(bookService, times(1)).getBookById("999");
        verify(bookService, times(0)).deleteBook("999");
    }
}
