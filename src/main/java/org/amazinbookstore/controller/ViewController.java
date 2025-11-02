package org.amazinbookstore.controller;

import org.amazinbookstore.exception.ResourceNotFoundException;
import org.amazinbookstore.model.Book;
import org.amazinbookstore.model.User;
import org.amazinbookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ViewController {
    private final BookService bookService;


    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(required = false) String search,
                       @RequestParam(required = false) String genre,
                       @RequestParam(required = false, defaultValue = "title") String sortBy,
                       @RequestParam(required = false, defaultValue = "asc") String order) {
        List<Book> books;

        String sortParam = sortBy;
        if (order != null && order.equalsIgnoreCase("desc") &&
            (sortBy.equals("price") || sortBy.equals("year"))) {
            sortParam = sortBy + "_desc";
        }

        if (search != null && !search.trim().isEmpty()) {
            books = bookService.searchBooks(null, null, null, search, sortParam);
        } else if (genre != null && !genre.trim().isEmpty()) {
            books = bookService.searchBooks(null, null, genre, null, sortParam);
        } else {
            books = bookService.searchBooks(null, null, null, null, sortParam);
        }

        model.addAttribute("books", books);
        model.addAttribute("currentSearch", search);
        model.addAttribute("currentGenre", genre);

        return "index";
    }

    @GetMapping("/admin")
    public String adminPanel(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        model.addAttribute("newBook", new Book());
        return "admin";
    }

    @GetMapping("/admin/book/edit/{id}")
    public String editBookForm(@PathVariable String id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "edit-book";
    }




}
