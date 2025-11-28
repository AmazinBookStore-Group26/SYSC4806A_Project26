package org.amazinbookstore.service;

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
     * Get book recommendations for a user based on Jaccard distance.
     * Finds users with similar purchase history and recommends books they bought
     * that the current user hasn't purchased yet.
     */
    public List<Book> getRecommendations(String userId, int maxRecommendations) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (currentUser.getPurchasedBookIds().isEmpty()) {
            // user has no purchases, cant recommend anything
            return new ArrayList<>();
        }

        Set<String> currentUserBooks = new HashSet<>(currentUser.getPurchasedBookIds());

        // calculate similiarity for all users
        List<User> allUsers = userRepository.findAll();
        Map<String, Double> userSimilarities = new HashMap<>();

        for (User otherUser : allUsers) {
            //skip current user and users that havnt purhased anything
            if (otherUser.getId().equals(userId) || otherUser.getPurchasedBookIds().isEmpty()) {
                continue;
            }

            // convert to set to get rid of duplciates
            Set<String> otherUserBooks = new HashSet<>(otherUser.getPurchasedBookIds());
            double similarity = calculateJaccardSimilarity(currentUserBooks, otherUserBooks);

            if (similarity > 0) {
                userSimilarities.put(otherUser.getId(), similarity);
            }
        }

        // sort by descending similiarity
        List<Map.Entry<String, Double>> sortedUsers = userSimilarities.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toList());

        //go through all the users and get the books they bought
        Set<String> recommendedBookIds = new LinkedHashSet<>();
        for (Map.Entry<String, Double> entry : sortedUsers) {
            User similarUser = userRepository.findById(entry.getKey()).orElse(null);
            if (similarUser == null) continue;

            for (String bookId : similarUser.getPurchasedBookIds()) {
                // recommend books the current user hasn't already bought
                if (!currentUserBooks.contains(bookId)) {
                    recommendedBookIds.add(bookId);
                }
            }

            // stop if we reach the max amount of books
            if (recommendedBookIds.size() >= maxRecommendations) {
                break;
            }
        }

        List<Book> recommendations = new ArrayList<>();
        for (String bookId : recommendedBookIds) {
            try {
                Book book = bookService.getBookById(bookId);
                recommendations.add(book);
                if (recommendations.size() >= maxRecommendations) {
                    break;
                }
            } catch (ResourceNotFoundException e) {
                // skip books that dont exist anymore
            }
        }

        return recommendations;
    }

    /**
     * Calculate Jaccard similarity between two sets.
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
