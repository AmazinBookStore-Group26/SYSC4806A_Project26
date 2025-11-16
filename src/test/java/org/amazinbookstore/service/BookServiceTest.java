package org.amazinbookstore.service;

import org.amazinbookstore.model.Book;
import org.amazinbookstore.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookService.
 * Tests all business logic for book search, filtering, sorting, and CRUD operations.
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book1;
    private Book book2;
    private Book book3;

    /**
     * Sets up test data before each test.
     * Initializes three sample books with different attributes for testing filtering and sorting.
     */
    @BeforeEach
    void setUp() {
        book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "Scribner", "978-0743273565", new BigDecimal("15.99"));
        book1.setId("1");
        book1.setGenre("Fiction");
        book1.setPublicationYear(1925);

        book2 = new Book("1984", "George Orwell", "Signet Classic", "978-0451524935", new BigDecimal("12.99"));
        book2.setId("2");
        book2.setGenre("Fiction");
        book2.setPublicationYear(1949);

        book3 = new Book("To Kill a Mockingbird", "Harper Lee", "Harper Perennial", "978-0060935467", new BigDecimal("18.99"));
        book3.setId("3");
        book3.setGenre("Fiction");
        book3.setPublicationYear(1960);
    }

    /**
     * Tests searching for books by author.
     * Should return books matching the author name with case-insensitive partial matching.
     */
    @Test
    void testSearchBooks_WithAuthor() {
        when(bookRepository.findByAuthorContainingIgnoreCase("Orwell"))
                .thenReturn(Arrays.asList(book2));

        List<Book> result = bookService.searchBooks("Orwell", null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("1984", result.get(0).getTitle());
        verify(bookRepository, times(1)).findByAuthorContainingIgnoreCase("Orwell");
    }

    /**
     * Tests searching for books by publisher.
     * Should return books matching the publisher name with case-insensitive partial matching.
     */
    @Test
    void testSearchBooks_WithPublisher() {
        when(bookRepository.findByPublisherContainingIgnoreCase("Harper"))
                .thenReturn(Arrays.asList(book3));

        List<Book> result = bookService.searchBooks(null, "Harper", null, null, null);

        assertEquals(1, result.size());
        assertEquals("To Kill a Mockingbird", result.get(0).getTitle());
        verify(bookRepository, times(1)).findByPublisherContainingIgnoreCase("Harper");
    }

    /**
     * Tests searching for books by genre.
     * Should return all books matching the genre with case-insensitive partial matching.
     */
    @Test
    void testSearchBooks_WithGenre() {
        when(bookRepository.findByGenreContainingIgnoreCase("Fiction"))
                .thenReturn(Arrays.asList(book1, book2, book3));

        List<Book> result = bookService.searchBooks(null, null, "Fiction", null, null);

        assertEquals(3, result.size());
        verify(bookRepository, times(1)).findByGenreContainingIgnoreCase("Fiction");
    }

    /**
     * Tests searching for books by both author and publisher.
     * Should return books matching both criteria.
     */
    @Test
    void testSearchBooks_WithAuthorAndPublisher() {
        when(bookRepository.findByAuthorAndPublisher("Fitzgerald", "Scribner"))
                .thenReturn(Arrays.asList(book1));

        List<Book> result = bookService.searchBooks("Fitzgerald", "Scribner", null, null, null);

        assertEquals(1, result.size());
        assertEquals("The Great Gatsby", result.get(0).getTitle());
        verify(bookRepository, times(1)).findByAuthorAndPublisher("Fitzgerald", "Scribner");
    }

    /**
     * Tests searching for books without any filters.
     * Should return all books in the repository.
     */
    @Test
    void testSearchBooks_NoFilters() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));

        List<Book> result = bookService.searchBooks(null, null, null, null, null);

        assertEquals(3, result.size());
        verify(bookRepository, times(1)).findAll();
    }

    /**
     * Tests sorting books by price in ascending order.
     * Should return books ordered from lowest to highest price.
     */
    @Test
    void testSearchBooks_SortByPrice() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));

        List<Book> result = bookService.searchBooks(null, null, null, null, "price");

        assertEquals(3, result.size());
        assertEquals(new BigDecimal("12.99"), result.get(0).getPrice());
        assertEquals(new BigDecimal("15.99"), result.get(1).getPrice());
        assertEquals(new BigDecimal("18.99"), result.get(2).getPrice());
    }

    /**
     * Tests sorting books by price in descending order.
     * Should return books ordered from highest to lowest price.
     */
    @Test
    void testSearchBooks_SortByPriceDesc() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));

        List<Book> result = bookService.searchBooks(null, null, null, null, "price_desc");

        assertEquals(3, result.size());
        assertEquals(new BigDecimal("18.99"), result.get(0).getPrice());
        assertEquals(new BigDecimal("15.99"), result.get(1).getPrice());
        assertEquals(new BigDecimal("12.99"), result.get(2).getPrice());
    }

    /**
     * Tests sorting books by title alphabetically.
     * Should return books ordered alphabetically by title (case-insensitive).
     */
    @Test
    void testSearchBooks_SortByTitle() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));

        List<Book> result = bookService.searchBooks(null, null, null, null, "title");

        assertEquals(3, result.size());
        assertEquals("1984", result.get(0).getTitle());
        assertEquals("The Great Gatsby", result.get(1).getTitle());
        assertEquals("To Kill a Mockingbird", result.get(2).getTitle());
    }

    /**
     * Tests retrieving all books without filtering or sorting.
     * Should return all books from the repository.
     */
    @Test
    void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));

        List<Book> result = bookService.getAllBooks();

        assertEquals(3, result.size());
        verify(bookRepository, times(1)).findAll();
    }

    /**
     * Tests retrieving a book by ID when the book exists.
     * Should return the book with matching ID.
     */
    @Test
    void testGetBookById_Found() {
        when(bookRepository.findById("1")).thenReturn(Optional.of(book1));

        Book result = bookService.getBookById("1");

        assertNotNull(result);
        assertEquals("The Great Gatsby", result.getTitle());
        verify(bookRepository, times(1)).findById("1");
    }

    /**
     * Tests retrieving a book by ID when the book does not exist.
     * Should return null.
     */
    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById("999")).thenReturn(Optional.empty());

        Book result = bookService.getBookById("999");

        assertNull(result);
        verify(bookRepository, times(1)).findById("999");
    }

    /**
     * Tests saving a book to the repository.
     * Should persist the book and return the saved instance.
     */
    @Test
    void testSaveBook() {
        when(bookRepository.save(book1)).thenReturn(book1);

        Book result = bookService.saveBook(book1);

        assertNotNull(result);
        assertEquals("The Great Gatsby", result.getTitle());
        verify(bookRepository, times(1)).save(book1);
    }

    /**
     * Tests deleting a book by ID.
     * Should remove the book from the repository.
     */
    @Test
    void testDeleteBook() {
        doNothing().when(bookRepository).deleteById("1");

        bookService.deleteBook("1");

        verify(bookRepository, times(1)).deleteById("1");
    }
}
