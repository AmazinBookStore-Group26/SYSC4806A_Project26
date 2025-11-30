package org.amazinbookstore.controller;

import org.amazinbookstore.exception.ResourceNotFoundException;
import org.amazinbookstore.model.Book;
import org.amazinbookstore.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the recommendations API endpoint.
 * just making sure the controller passes requests
 * to the service correctly and returns proper responses.
 */
class RecommendationControllerTest {

    private RecommendationService recommendationService;
    private RecommendationController recommendationController;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        recommendationService = mock(RecommendationService.class);
        recommendationController = new RecommendationController(recommendationService);

        book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "Scribner", "978-0743273565", new BigDecimal("15.99"));
        book1.setId("1");

        book2 = new Book("1984", "George Orwell", "Signet Classic", "978-0451524935", new BigDecimal("12.99"));
        book2.setId("2");
    }

    @Test
    void shouldReturnRecommendationsForHasib() {
        // Hasib exists and has some recommendations
        when(recommendationService.getRecommendations("hasib", 10))
                .thenReturn(Arrays.asList(book1, book2));

        ResponseEntity<List<Book>> response = recommendationController.getRecommendations("hasib", 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(recommendationService, times(1)).getRecommendations("hasib", 10);
    }

    @Test
    void shouldReturnEmptyListWhenNoRecommendations() {
        // Hajar exists but no recommendations available
        when(recommendationService.getRecommendations("hajar", 10))
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<Book>> response = recommendationController.getRecommendations("hajar", 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void shouldUseCustomLimit() {
        // check that the limit param actually gets passed through
        when(recommendationService.getRecommendations("hasib", 5))
                .thenReturn(Arrays.asList(book1));
        ResponseEntity<List<Book>> response = recommendationController.getRecommendations("hasib", 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // make sure we called with the right limit
        verify(recommendationService, times(1)).getRecommendations("hasib", 5);
    }

    @Test
    void shouldHandleUserNotFound() {
        // service throws when user doesn't exist
        when(recommendationService.getRecommendations("ghost", 10))
                .thenThrow(new ResourceNotFoundException("User not found with id: ghost"));

        assertThrows(ResourceNotFoundException.class, () -> {
            recommendationController.getRecommendations("ghost", 10);
        });
    }

    @Test
    void shouldReturnSingleRecommendation() {
        // just one book to recommend to Yusuf
        when(recommendationService.getRecommendations("yusuf", 10))
                .thenReturn(Arrays.asList(book1));

        ResponseEntity<List<Book>> response = recommendationController.getRecommendations("yusuf", 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("The Great Gatsby", response.getBody().get(0).getTitle());
    }

    @Test
    void shouldWorkWithLargeLimit() {
        // Hajar asking for lots of recommendations
        when(recommendationService.getRecommendations("hajar", 100))
                .thenReturn(Arrays.asList(book1, book2));

        ResponseEntity<List<Book>> response = recommendationController.getRecommendations("hajar", 100);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // even with high limit, we only get what's available
        assertEquals(2, response.getBody().size());
        verify(recommendationService).getRecommendations("hajar", 100);
    }
}
