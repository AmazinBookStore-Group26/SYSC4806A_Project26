package org.amazinbookstore.service;

import org.amazinbookstore.dto.RecommendationResponse;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the recommendation service.
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

        RecommendationResponse response = recommendationService.getRecommendations("hasib", 5);

        assertEquals(1, response.getBooks().size());
        assertEquals("book3", response.getBooks().get(0).getId());
        assertFalse(response.isFallback());
    }

    @Test
    void shouldFallbackToPopularBooksWhenUserHasNoPurchases() {
        // new user with no history should see popular books
        User newUser = new User();
        newUser.setId("newbie");
        newUser.setPurchasedBookIds(new ArrayList<>());

        when(userRepository.findById("newbie")).thenReturn(Optional.of(newUser));
        when(userRepository.findAll()).thenReturn(Arrays.asList(newUser, hajar, yusuf));
        when(bookService.getBookById("book1")).thenReturn(book1);
        when(bookService.getBookById("book2")).thenReturn(book2);
        when(bookService.getBookById("book3")).thenReturn(book3);
        when(bookService.getBookById("book4")).thenReturn(book4);

        RecommendationResponse response = recommendationService.getRecommendations("newbie", 5);

        assertFalse(response.getBooks().isEmpty());
        assertTrue(response.isFallback());
        assertTrue(response.getMessage().contains("couldn't find"));
    }

    @Test
    void shouldFallbackToPopularBooksWhenNoSimilarUsers() {
        // Hasib bought unique books that nobody else has
        User uniqueHasib = new User();
        uniqueHasib.setId("uniqueHasib");
        uniqueHasib.setUsername("uniqueHasib");
        uniqueHasib.setPurchasedBookIds(new ArrayList<>(Arrays.asList("rareBook1", "rareBook2")));

        // other users bought different stuff
        when(userRepository.findById("uniqueHasib")).thenReturn(Optional.of(uniqueHasib));
        when(userRepository.findAll()).thenReturn(Arrays.asList(uniqueHasib, hajar, yusuf));
        when(bookService.getBookById("book1")).thenReturn(book1);
        when(bookService.getBookById("book2")).thenReturn(book2);
        when(bookService.getBookById("book3")).thenReturn(book3);
        when(bookService.getBookById("book4")).thenReturn(book4);

        RecommendationResponse response = recommendationService.getRecommendations("uniqueHasib", 5);

        // should fall back to popular books since no one shares his taste
        assertTrue(response.isFallback());
        assertFalse(response.getBooks().isEmpty());
    }

    @Test
    void shouldReturnPopularBooksSortedByPurchaseCount() {
        // Hasib bought unique stuff, so we fall back to popular
        User uniqueHasib = new User();
        uniqueHasib.setId("uniqueHasib");
        uniqueHasib.setPurchasedBookIds(new ArrayList<>(Arrays.asList("rareBook")));

        // book1 was bought by 2 people, book4 by 1 person
        User buyer1 = new User();
        buyer1.setId("buyer1");
        buyer1.setPurchasedBookIds(new ArrayList<>(Arrays.asList("book1")));

        User buyer2 = new User();
        buyer2.setId("buyer2");
        buyer2.setPurchasedBookIds(new ArrayList<>(Arrays.asList("book1", "book4")));

        when(userRepository.findById("uniqueHasib")).thenReturn(Optional.of(uniqueHasib));
        when(userRepository.findAll()).thenReturn(Arrays.asList(uniqueHasib, buyer1, buyer2));
        when(bookService.getBookById("book1")).thenReturn(book1);
        when(bookService.getBookById("book4")).thenReturn(book4);

        RecommendationResponse response = recommendationService.getRecommendations("uniqueHasib", 5);

        // book1 should come first since 2 people bought it
        assertEquals(2, response.getBooks().size());
        assertEquals("book1", response.getBooks().get(0).getId());
        assertTrue(response.isFallback());
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
        // if there are a lot of recommendations should be capped to max
        User bookworm = new User();
        bookworm.setId("bookworm");
        bookworm.setPurchasedBookIds(new ArrayList<>(Arrays.asList("book1", "book3", "book4")));

        when(userRepository.findById("hasib")).thenReturn(Optional.of(hasib));
        when(userRepository.findAll()).thenReturn(Arrays.asList(hasib, bookworm));
        when(userRepository.findById("bookworm")).thenReturn(Optional.of(bookworm));
        when(bookService.getBookById("book3")).thenReturn(book3);

        RecommendationResponse response = recommendationService.getRecommendations("hasib", 1);

        assertEquals(1, response.getBooks().size());
    }

    @Test
    void shouldNotRecommendBooksUserAlreadyOwns() {
        // make sure we don't suggest books Hasib already has
        when(userRepository.findById("hasib")).thenReturn(Optional.of(hasib));
        when(userRepository.findAll()).thenReturn(Arrays.asList(hasib, hajar));
        when(userRepository.findById("hajar")).thenReturn(Optional.of(hajar));
        when(bookService.getBookById("book3")).thenReturn(book3);

        RecommendationResponse response = recommendationService.getRecommendations("hasib", 10);

        // should only get book3 not book1 or book2 which Hasib already owns
        for (Book book : response.getBooks()) {
            assertNotEquals("book1", book.getId());
            assertNotEquals("book2", book.getId());
        }
    }

    @Test
    void shouldHandleDeletedBooksAndFallbackIfNeeded() {
        // if similar user's books are all deleted, fall back to popular
        when(userRepository.findById("hasib")).thenReturn(Optional.of(hasib));
        when(userRepository.findAll()).thenReturn(Arrays.asList(hasib, hajar, yusuf));
        when(userRepository.findById("hajar")).thenReturn(Optional.of(hajar));
        when(bookService.getBookById("book3")).thenThrow(new ResourceNotFoundException("Book not found"));
        when(bookService.getBookById("book4")).thenReturn(book4);

        RecommendationResponse response = recommendationService.getRecommendations("hasib", 5);

        // book3 is deleted, so we fall back to popular (book4 from yusuf)
        assertTrue(response.isFallback());
        assertEquals(1, response.getBooks().size());
        assertEquals("book4", response.getBooks().get(0).getId());
    }

    @Test
    void shouldPrioritizeMoreSimilarUsers() {
        // user with higher jaccard similarity should have their books recommended first
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

        RecommendationResponse response = recommendationService.getRecommendations("hasib", 5);

        // book4 should come before book3 because hajarSuperFan is more similar to Hasib
        assertEquals(2, response.getBooks().size());
        assertEquals("book4", response.getBooks().get(0).getId());
        assertEquals("book3", response.getBooks().get(1).getId());
        assertFalse(response.isFallback());
    }

    @Test
    void shouldFallbackWhenOnlyUserInSystem() {
        // Hasib is the only user - no one to compare with
        when(userRepository.findById("hasib")).thenReturn(Optional.of(hasib));
        when(userRepository.findAll()).thenReturn(Arrays.asList(hasib));

        RecommendationResponse response = recommendationService.getRecommendations("hasib", 5);

        // no other users means empty (can't even do popular since no one else bought anything)
        assertTrue(response.getBooks().isEmpty());
    }

    @Test
    void shouldIgnoreUsersWithNoPurchases() {
        // lurkers who haven't bought anything shouldn't affect recommendations
        User lurker = new User();
        lurker.setId("lurker");
        lurker.setPurchasedBookIds(new ArrayList<>());

        when(userRepository.findById("hasib")).thenReturn(Optional.of(hasib));
        when(userRepository.findAll()).thenReturn(Arrays.asList(hasib, lurker, hajar));
        when(userRepository.findById("hajar")).thenReturn(Optional.of(hajar));
        when(bookService.getBookById("book3")).thenReturn(book3);

        RecommendationResponse response = recommendationService.getRecommendations("hasib", 5);

        // should still work, just ignoring the lurker
        assertEquals(1, response.getBooks().size());
        assertEquals("book3", response.getBooks().get(0).getId());
        assertFalse(response.isFallback());
    }

    @Test
    void shouldExcludeOwnedBooksFromPopularFallback() {
        // when falling back to popular, don't recommend books user already has
        User uniqueHasib = new User();
        uniqueHasib.setId("uniqueHasib");
        uniqueHasib.setPurchasedBookIds(new ArrayList<>(Arrays.asList("book1"))); // owns book1

        User buyer = new User();
        buyer.setId("buyer");
        buyer.setPurchasedBookIds(new ArrayList<>(Arrays.asList("book3", "book4"))); // no overlap

        when(userRepository.findById("uniqueHasib")).thenReturn(Optional.of(uniqueHasib));
        when(userRepository.findAll()).thenReturn(Arrays.asList(uniqueHasib, buyer));
        when(bookService.getBookById("book3")).thenReturn(book3);
        when(bookService.getBookById("book4")).thenReturn(book4);

        RecommendationResponse response = recommendationService.getRecommendations("uniqueHasib", 5);

        // should not include book1 since uniqueHasib already owns it
        for (Book book : response.getBooks()) {
            assertNotEquals("book1", book.getId());
        }
        assertTrue(response.isFallback());
    }
}
