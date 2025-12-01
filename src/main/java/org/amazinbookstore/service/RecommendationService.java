package org.amazinbookstore.service;

import org.amazinbookstore.dto.RecommendationResponse;
import org.amazinbookstore.exception.ResourceNotFoundException;
import org.amazinbookstore.model.Book;
import org.amazinbookstore.model.User;
import org.amazinbookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserRepository userRepository;
    private final BookService bookService;

    /**
     * Get book recommendations for a user based on Jaccard similarity.
     * If no similar users are found, falls back to popular books.
     */
    public RecommendationResponse getRecommendations(String userId, int maxRecommendations) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Set<String> currentUserBooks = new HashSet<>(currentUser.getPurchasedBookIds());

        // if user has no purchases, just show popular books
        if (currentUserBooks.isEmpty()) {
            List<Book> popularBooks = getPopularBooks(maxRecommendations, currentUserBooks);
            if (popularBooks.isEmpty()) {
                return RecommendationResponse.empty();
            }
            return RecommendationResponse.fallbackToPopular(popularBooks);
        }

        // try to find similar users
        List<User> allUsers = userRepository.findAll();
        Map<String, Double> userSimilarities = new HashMap<>();

        for (User otherUser : allUsers) {
            if (otherUser.getId().equals(userId) || otherUser.getPurchasedBookIds().isEmpty()) {
                continue;
            }

            Set<String> otherUserBooks = new HashSet<>(otherUser.getPurchasedBookIds());
            double similarity = calculateJaccardSimilarity(currentUserBooks, otherUserBooks);

            if (similarity > 0) {
                userSimilarities.put(otherUser.getId(), similarity);
            }
        }

        // no similar users found - fall back to popular books
        if (userSimilarities.isEmpty()) {
            List<Book> popularBooks = getPopularBooks(maxRecommendations, currentUserBooks);
            if (popularBooks.isEmpty()) {
                return RecommendationResponse.empty();
            }
            return RecommendationResponse.fallbackToPopular(popularBooks);
        }

        // we have similar users, get their books
        List<Map.Entry<String, Double>> sortedUsers = userSimilarities.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toList());

        Set<String> recommendedBookIds = new LinkedHashSet<>();
        for (Map.Entry<String, Double> entry : sortedUsers) {
            User similarUser = userRepository.findById(entry.getKey()).orElse(null);
            if (similarUser == null) continue;

            for (String bookId : similarUser.getPurchasedBookIds()) {
                if (!currentUserBooks.contains(bookId)) {
                    recommendedBookIds.add(bookId);
                }
            }

            if (recommendedBookIds.size() >= maxRecommendations) {
                break;
            }
        }

        List<Book> recommendations = fetchBooks(recommendedBookIds, maxRecommendations);

        // edge case: similar users exist but all their books are ones we already own
        if (recommendations.isEmpty()) {
            List<Book> popularBooks = getPopularBooks(maxRecommendations, currentUserBooks);
            if (popularBooks.isEmpty()) {
                return RecommendationResponse.empty();
            }
            return RecommendationResponse.fallbackToPopular(popularBooks);
        }

        return RecommendationResponse.personalized(recommendations);
    }

    /**
     * Get popular books based on how many users have purchased them.
     * Excludes books the user already owns.
     */
    private List<Book> getPopularBooks(int maxBooks, Set<String> excludeBookIds) {
        List<User> allUsers = userRepository.findAll();

        // count how many times each book was purchased
        Map<String, Long> bookPurchaseCounts = allUsers.stream()
                .flatMap(user -> user.getPurchasedBookIds().stream())
                .filter(bookId -> !excludeBookIds.contains(bookId))
                .collect(Collectors.groupingBy(bookId -> bookId, Collectors.counting()));

        // sort by purchase count descending
        List<String> popularBookIds = bookPurchaseCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(maxBooks)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return fetchBooks(new LinkedHashSet<>(popularBookIds), maxBooks);
    }

    /**
     * Fetch actual Book objects from a set of IDs.
     * Skips books that no longer exist.
     */
    private List<Book> fetchBooks(Set<String> bookIds, int maxBooks) {
        List<Book> books = new ArrayList<>();
        for (String bookId : bookIds) {
            try {
                Book book = bookService.getBookById(bookId);
                if (book != null) {
                    books.add(book);
                    if (books.size() >= maxBooks) {
                        break;
                    }
                }
            } catch (ResourceNotFoundException e) {
                // book was deleted, skip it
            }
        }
        return books;
    }

    /**
     * Jaccard similarity = |A ∩ B| / |A ∪ B|
     */
    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        if (set1.isEmpty() && set2.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }
}
