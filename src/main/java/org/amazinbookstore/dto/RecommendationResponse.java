package org.amazinbookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.amazinbookstore.model.Book;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {

    private List<Book> books;

    // true if we couldn't find similar users and fell back to popular books
    private boolean fallback;

    // message to display to the user
    private String message;

    public static RecommendationResponse personalized(List<Book> books) {
        return new RecommendationResponse(books, false, "Based on your reading history");
    }

    public static RecommendationResponse fallbackToPopular(List<Book> books) {
        return new RecommendationResponse(books, true, "We couldn't find readers with similar taste, here are some popular books");
    }

    public static RecommendationResponse empty() {
        return new RecommendationResponse(List.of(), false, "No recommendations available");
    }
}
