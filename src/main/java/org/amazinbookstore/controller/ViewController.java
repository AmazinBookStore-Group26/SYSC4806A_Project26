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
