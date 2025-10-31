package org.amazinbookstore.service;

import org.amazinbookstore.model.Book;
import org.amazinbookstore.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Search and filter books based on provided criteria
     * Supports filtering by author, publisher, genre, and title
     * Supports sorting by price, title, author, or publicationYear
     */
    public List<Book> searchBooks(String author, String publisher, String genre, String title, String sortBy) {
        List<Book> books;

        // Apply filters
        if (author != null && publisher != null) {
            books = bookRepository.findByAuthorAndPublisher(author, publisher);
        } else if (author != null) {
            books = bookRepository.findByAuthorContainingIgnoreCase(author);
        } else if (publisher != null) {
            books = bookRepository.findByPublisherContainingIgnoreCase(publisher);
        } else if (genre != null) {
            books = bookRepository.findByGenreContainingIgnoreCase(genre);
        } else if (title != null) {
            books = bookRepository.findByTitleContainingIgnoreCase(title);
        } else {
            books = bookRepository.findAll();
        }

        // Apply sorting
        if (sortBy != null && !books.isEmpty()) {
            books = sortBooks(books, sortBy);
        }

        return books;
    }

    /**
     * Sort books based on the specified field
     */
    private List<Book> sortBooks(List<Book> books, String sortBy) {
        Comparator<Book> comparator;

        switch (sortBy.toLowerCase()) {
            case "price":
                comparator = Comparator.comparing(Book::getPrice, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case "price_desc":
                comparator = Comparator.comparing(Book::getPrice, Comparator.nullsLast(Comparator.reverseOrder()));
                break;
            case "title":
                comparator = Comparator.comparing(Book::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                break;
            case "author":
                comparator = Comparator.comparing(Book::getAuthor, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                break;
            case "year":
                comparator = Comparator.comparing(Book::getPublicationYear, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case "year_desc":
                comparator = Comparator.comparing(Book::getPublicationYear, Comparator.nullsLast(Comparator.reverseOrder()));
                break;
            default:
                return books; // No sorting if invalid sortBy parameter
        }

        return books.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * Get all books
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Get book by ID
     */
    public Book getBookById(String id) {
        return bookRepository.findById(id).orElse(null);
    }

    /**
     * Save a new book or update existing one
     */
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    /**
     * Delete a book by ID
     */
    public void deleteBook(String id) {
        bookRepository.deleteById(id);
    }
}
