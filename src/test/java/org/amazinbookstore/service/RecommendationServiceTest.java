package org.amazinbookstore.service;

import org.amazinbookstore.exception.ResourceNotFoundException;
import org.amazinbookstore.model.Book;
import org.amazinbookstore.model.User;
import org.amazinbookstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the recommendation servie.
 */
@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private RecommendationService recommendationService;

    private User hasib;
    private User hajar;
    private User yusuf;
    private Book book1;
    private Book book2;
    private Book book3;
    private Book book4;

    @BeforeEach
    void setUp() {
        // set up some books to work with
        book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "Scribner", "978-0743273565", new BigDecimal("15.99"));
        book1.setId("book1");

        book2 = new Book("1984", "George Orwell", "Signet Classic", "978-0451524935", new BigDecimal("12.99"));
        book2.setId("book2");

        book3 = new Book("To Kill a Mockingbird", "Harper Lee", "Harper Perennial", "978-0060935467", new BigDecimal("18.99"));
        book3.setId("book3");

        book4 = new Book("Pride and Prejudice", "Jane Austen", "Penguin", "978-0141439518", new BigDecimal("10.99"));
        book4.setId("book4");

        // main user bought books 1 and 2
        hasib = new User();
        hasib.setId("hasib");
        hasib.setUsername("hasib");
        hasib.setPurchasedBookIds(new ArrayList<>(Arrays.asList("book1", "book2")));

        // Hajar bought book1 and book2 and book3
        hajar = new User();
        hajar.setId("hajar");
        hajar.setUsername("hajar");
        hajar.setPurchasedBookIds(new ArrayList<>(Arrays.asList("book1", "book2", "book3")));

        // Yusuf bought completely different stuff
        yusuf = new User();
        yusuf.setId("yusuf");
        yusuf.setUsername("yusuf");
        yusuf.setPurchasedBookIds(new ArrayList<>(Arrays.asList("book4")));
    }

    @Test
    void shouldRecommendBooksFromSimilarUsers() {
        // should recommend book3 to hasib since hajar bought similar books
        when(userRepository.findById("hasib")).thenReturn(Optional.of(hasib));
        when(userRepository.findAll()).thenReturn(Arrays.asList(hasib, hajar, yusuf));
        when(userRepository.findById("hajar")).thenReturn(Optional.of(hajar));
        when(bookService.getBookById("book3")).thenReturn(book3);

        List<Book> recommendations = recommendationService.getRecommendations("hasib", 5);

        assertEquals(1, recommendations.size());
        assertEquals("book3", recommendations.get(0).getId());
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoPurchases() {
        // can't recommend anything if user hasn't bought anything yet
        User newUser = new User();
        newUser.setId("newbie");
        newUser.setPurchasedBookIds(new ArrayList<>());

        when(userRepository.findById("newbie")).thenReturn(Optional.of(newUser));
        List<Book> recommendations = recommendationService.getRecommendations("newbie", 5);
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById("ghost")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            recommendationService.getRecommendations("ghost", 5);
        });
    }

    @Test
    void shouldRespectMaxRecommendationsLimit() {
        // if htere are alot of recommendations should be capped to max
        User bookworm = new User();
        bookworm.setId("bookworm");
        bookworm.setPurchasedBookIds(new ArrayList<>(Arrays.asList("book1", "book3", "book4")));

        when(userRepository.findById("hasib")).thenReturn(Optional.of(hasib));
        when(userRepository.findAll()).thenReturn(Arrays.asList(hasib, bookworm));
        when(userRepository.findById("bookworm")).thenReturn(Optional.of(bookworm));
        when(bookService.getBookById("book3")).thenReturn(book3);
        // not stubbing book4 because we only ask for 1 and book3 comes first

        // only ask for 1 recommendation even though there are 2 available
        List<Book> recommendations = recommendationService.getRecommendations("hasib", 1);

        assertEquals(1, recommendations.size());
    }

    @Test
    void shouldNotRecommendBooksUserAlreadyOwns() {
        // make sure we don't suggest books Hasib already has
        when(userRepository.findById("hasib")).thenReturn(Optional.of(hasib));
        when(userRepository.findAll()).thenReturn(Arrays.asList(hasib, hajar));
        when(userRepository.findById("hajar")).thenReturn(Optional.of(hajar));
        when(bookService.getBookById("book3")).thenReturn(book3);

        List<Book> recommendations = recommendationService.getRecommendations("hasib", 10);

        // should only get book3 not book1 or book2 which Hasib already owns
        for (Book book : recommendations) {
            assertNotEquals("book1", book.getId());
            assertNotEquals("book2", book.getId());
        }
    }

    @Test
    void shouldHandleDeletedBooks() {
        // if a book was deleted from the system, we should just skip it
        when(userRepository.findById("hasib")).thenReturn(Optional.of(hasib));
        when(userRepository.findAll()).thenReturn(Arrays.asList(hasib, hajar));
        when(userRepository.findById("hajar")).thenReturn(Optional.of(hajar));
        when(bookService.getBookById("book3")).thenThrow(new ResourceNotFoundException("Book not found"));

        // should not raise an error, just return empty since the only candidate book is gone
        List<Book> recommendations = recommendationService.getRecommendations("hasib", 5);
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void shouldPrioritizeMoreSimilarUsers() {
        // Hajar has higher jaccard similarity so her books should be recommended first
        User hajarSuperFan = new User();
        hajarSuperFan.setId("hajarSuperFan");
        hajarSuperFan.setPurchasedBookIds(new ArrayList<>(Arrays.asList("book1", "book2", "book4")));

        // Yusuf is less similar
        User yusufCasual = new User();
        yusufCasual.setId("yusufCasual");
        yusufCasual.setPurchasedBookIds(new ArrayList<>(Arrays.asList("book1", "book3")));

        when(userRepository.findById("hasib")).thenReturn(Optional.of(hasib));
        when(userRepository.findAll()).thenReturn(Arrays.asList(hasib, hajarSuperFan, yusufCasual));
        when(userRepository.findById("hajarSuperFan")).thenReturn(Optional.of(hajarSuperFan));
        when(userRepository.findById("yusufCasual")).thenReturn(Optional.of(yusufCasual));
        when(bookService.getBookById("book4")).thenReturn(book4);
        when(bookService.getBookById("book3")).thenReturn(book3);

        List<Book> recommendations = recommendationService.getRecommendations("hasib", 5);

        // book4 should come before book3 because hajarSuperFan is more similar to Hasib
        assertEquals(2, recommendations.size());
        assertEquals("book4", recommendations.get(0).getId());
        assertEquals("book3", recommendations.get(1).getId());
    }

    @Test
    void shouldReturnEmptyWhenNoOtherUsersExist() {
        // Hasib is the only user in the system
        when(userRepository.findById("hasib")).thenReturn(Optional.of(hasib));
        when(userRepository.findAll()).thenReturn(Arrays.asList(hasib));

        List<Book> recommendations = recommendationService.getRecommendations("hasib", 5);
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void shouldIgnoreUsersWithNoPurchases() {
        // users who haven't bought anything shouldn't affect recommendations
        User lurker = new User();
        lurker.setId("lurker");
        lurker.setPurchasedBookIds(new ArrayList<>());

        when(userRepository.findById("hasib")).thenReturn(Optional.of(hasib));
        when(userRepository.findAll()).thenReturn(Arrays.asList(hasib, lurker, hajar));
        when(userRepository.findById("hajar")).thenReturn(Optional.of(hajar));
        when(bookService.getBookById("book3")).thenReturn(book3);

        List<Book> recommendations = recommendationService.getRecommendations("hasib", 5);

        // should still work, just ignoring the lurker
        assertEquals(1, recommendations.size());
        assertEquals("book3", recommendations.get(0).getId());
    }
}
