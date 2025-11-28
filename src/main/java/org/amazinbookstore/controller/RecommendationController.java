package org.amazinbookstore.controller;

import org.amazinbookstore.model.Book;
import org.amazinbookstore.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Endpoint for getting recommended books for a user
     * @param userId ID of current user
     * @param limit max amount of recommended books
     * @return recommended books
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<Book>> getRecommendations(
            @PathVariable String userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<Book> recommendations = recommendationService.getRecommendations(userId, limit);
        return ResponseEntity.ok(recommendations);
    }
}
