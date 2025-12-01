package org.amazinbookstore.controller;

import org.amazinbookstore.dto.RecommendationResponse;
import org.amazinbookstore.exception.ResourceNotFoundException;
import org.amazinbookstore.model.Book;
import org.amazinbookstore.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the recommendations API endpoint.
 * Making sure the controller passes requests to the service
 * and returns proper responses with fallback info.
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
    void shouldReturnPersonalizedRecommendationsForHasib() {
        // Hasib has similar users, so we get personalized recs
        RecommendationResponse serviceResponse = RecommendationResponse.personalized(Arrays.asList(book1, book2));
        when(recommendationService.getRecommendations("hasib", 10)).thenReturn(serviceResponse);

        ResponseEntity<RecommendationResponse> response = recommendationController.getRecommendations("hasib", 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getBooks().size());
        assertFalse(response.getBody().isFallback());
        verify(recommendationService, times(1)).getRecommendations("hasib", 10);
    }

    @Test
    void shouldReturnFallbackRecommendationsWithMessage() {
        // Hajar has unique taste so we fall back to popular books
        RecommendationResponse serviceResponse = RecommendationResponse.fallbackToPopular(Arrays.asList(book1));
        when(recommendationService.getRecommendations("hajar", 10)).thenReturn(serviceResponse);

        ResponseEntity<RecommendationResponse> response = recommendationController.getRecommendations("hajar", 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isFallback());
        assertTrue(response.getBody().getMessage().contains("couldn't find"));
    }

    @Test
    void shouldReturnEmptyResponseWhenNoBooksAvailable() {
        // no books to recommend at all
        RecommendationResponse serviceResponse = RecommendationResponse.empty();
        when(recommendationService.getRecommendations("yusuf", 10)).thenReturn(serviceResponse);

        ResponseEntity<RecommendationResponse> response = recommendationController.getRecommendations("yusuf", 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getBooks().isEmpty());
    }

    @Test
    void shouldUseCustomLimit() {
        // check that the limit param gets passed through
        RecommendationResponse serviceResponse = RecommendationResponse.personalized(Arrays.asList(book1));
        when(recommendationService.getRecommendations("hasib", 5)).thenReturn(serviceResponse);

        ResponseEntity<RecommendationResponse> response = recommendationController.getRecommendations("hasib", 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
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
    void shouldIncludeMessageInResponse() {
        // make sure the message field is populated for UI to display
        RecommendationResponse serviceResponse = RecommendationResponse.personalized(Arrays.asList(book1));
        when(recommendationService.getRecommendations("hasib", 10)).thenReturn(serviceResponse);

        ResponseEntity<RecommendationResponse> response = recommendationController.getRecommendations("hasib", 10);

        assertNotNull(response.getBody().getMessage());
        assertFalse(response.getBody().getMessage().isEmpty());
    }
}
