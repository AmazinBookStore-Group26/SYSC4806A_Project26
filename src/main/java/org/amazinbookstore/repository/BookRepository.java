package org.amazinbookstore.repository;

import org.amazinbookstore.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByPublisherContainingIgnoreCase(String publisher);

    List<Book> findByGenreContainingIgnoreCase(String genre);

    List<Book> findByTitleContainingIgnoreCase(String title);

    @Query("{ 'author': { $regex: ?0, $options: 'i' }, 'publisher': { $regex: ?1, $options: 'i' } }")
    List<Book> findByAuthorAndPublisher(String author, String publisher);
}
