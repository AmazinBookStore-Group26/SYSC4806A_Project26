package org.amazinbookstore.controller;

import org.amazinbookstore.dto.RecommendationResponse;
import org.amazinbookstore.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Get recommended books for a user.
     * Returns personalized recommendations if we find similar users,
     * otherwise falls back to popular books with a message explaining that.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @PathVariable String userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        RecommendationResponse response = recommendationService.getRecommendations(userId, limit);
        return ResponseEntity.ok(response);
    }
}
