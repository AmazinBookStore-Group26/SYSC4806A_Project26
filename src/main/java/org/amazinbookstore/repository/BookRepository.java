package org.amazinbookstore.repository;

import org.amazinbookstore.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for performing CRUD and query operations on
 * {@link Book} documents stored in MongoDB.
 *
 * Provides several case-insensitive search helpers commonly used by the
 * store browsing and search features.
 */
@Repository
public interface BookRepository extends MongoRepository<Book, String> {

    /**
     * Finds books by a partial (case-insensitive) match on author name.
     *
     * @param author the author substring to match
     * @return matching books
     */
    List<Book> findByAuthorContainingIgnoreCase(String author);

    /**
     * Finds books by a partial (case-insensitive) match on publisher name.
     *
     * @param publisher the publisher substring to match
     * @return matching books
     */
    List<Book> findByPublisherContainingIgnoreCase(String publisher);

    /**
     * Finds books by a partial (case-insensitive) match on genre.
     *
     * @param genre the genre substring to match
     * @return matching books
     */
    List<Book> findByGenreContainingIgnoreCase(String genre);

    /**
     * Finds books by a partial (case-insensitive) match on title.
     *
     * @param title the title substring to match
     * @return matching books
     */
    List<Book> findByTitleContainingIgnoreCase(String title);

    /**
     * Custom query: finds books whose author and publisher both match
     * the given case-insensitive regex patterns.
     *
     * @param author    regex pattern for author
     * @param publisher regex pattern for publisher
     * @return matching books
     */
    @Query("{ 'author': { $regex: ?0, $options: 'i' }, 'publisher': { $regex: ?1, $options: 'i' } }")
    List<Book> findByAuthorAndPublisher(String author, String publisher);
}
