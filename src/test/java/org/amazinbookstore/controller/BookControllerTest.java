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

class BookControllerTest {

    private BookService bookService;
    private BookController bookController;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        bookService = mock(BookService.class);
        bookController = new BookController(bookService);

        book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "Scribner", "978-0743273565", new BigDecimal("15.99"));
        book1.setId("1");

        book2 = new Book("1984", "George Orwell", "Signet Classic", "978-0451524935", new BigDecimal("12.99"));
        book2.setId("2");
    }

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

    @Test
    void testGetBookById_Found() {
        when(bookService.getBookById("1")).thenReturn(book1);

        ResponseEntity<Book> response = bookController.getBookById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The Great Gatsby", response.getBody().getTitle());
        verify(bookService, times(1)).getBookById("1");
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookService.getBookById("999")).thenReturn(null);

        ResponseEntity<Book> response = bookController.getBookById("999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(bookService, times(1)).getBookById("999");
    }

    @Test
    void testCreateBook() {
        when(bookService.saveBook(any(Book.class))).thenReturn(book1);

        ResponseEntity<Book> response = bookController.createBook(book1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The Great Gatsby", response.getBody().getTitle());
        verify(bookService, times(1)).saveBook(any(Book.class));
    }

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

    @Test
    void testUpdateBook_NotFound() {
        when(bookService.getBookById("999")).thenReturn(null);

        ResponseEntity<Book> response = bookController.updateBook("999", book1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(bookService, times(1)).getBookById("999");
        verify(bookService, times(0)).saveBook(any(Book.class));
    }

    @Test
    void testDeleteBook_Success() {
        when(bookService.getBookById("1")).thenReturn(book1);
        doNothing().when(bookService).deleteBook("1");

        ResponseEntity<Void> response = bookController.deleteBook("1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookService, times(1)).getBookById("1");
        verify(bookService, times(1)).deleteBook("1");
    }

    @Test
    void testDeleteBook_NotFound() {
        when(bookService.getBookById("999")).thenReturn(null);

        ResponseEntity<Void> response = bookController.deleteBook("999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(bookService, times(1)).getBookById("999");
        verify(bookService, times(0)).deleteBook("999");
    }
}
