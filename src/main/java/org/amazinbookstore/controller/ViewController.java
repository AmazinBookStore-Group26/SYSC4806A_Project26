package org.amazinbookstore.controller;

import org.amazinbookstore.exception.ResourceNotFoundException;
import org.amazinbookstore.model.Book;
import org.amazinbookstore.model.ShoppingCart;
import org.amazinbookstore.model.User;
import org.amazinbookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.amazinbookstore.service.ShoppingCartService;
import org.amazinbookstore.service.UserService;
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
    private final ShoppingCartService shoppingCartService;
    private final UserService userService;

    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(required = false) String search,
                       @RequestParam(required = false) String genre,
                       @RequestParam(required = false, defaultValue = "title") String sortBy,
                       @RequestParam(required = false, defaultValue = "asc") String order,
                       Principal principal) {
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

        // Add userId for authenticated users
        if (principal != null) {
            User user = getCurrentUser(principal);
            model.addAttribute("userId", user.getId());
        }

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


    @GetMapping("/cart")
    public String viewCart(Principal principal, Model model) {
        User user = getCurrentUser(principal);
        String userId = user.getId();
        ShoppingCart cart = shoppingCartService.getCartByUserId(userId);

        // Get full book details for each cart item, skip if book was deleted
        List<Book> books = new ArrayList<>();
        List<Integer> validIndices = new ArrayList<>();

        for (int i = 0; i < cart.getItems().size(); i++) {
            try {
                Book book = bookService.getBookById(cart.getItems().get(i).getBookId());
                books.add(book);
                validIndices.add(i);
            } catch (ResourceNotFoundException e) {
                // Book was deleted, skip it
            }
        }

        model.addAttribute("cart", cart);
        model.addAttribute("books", books);
        model.addAttribute("userId", userId);

        // Calculate total
        double total = 0.0;
        for (int i = 0; i < books.size(); i++) {
            int cartIndex = validIndices.get(i);
            total += books.get(i).getPrice().doubleValue() * cart.getItems().get(cartIndex).getQuantity();
        }
        model.addAttribute("total", total);

        return "cart";
    }


    // Helper method to get the currently authenticated user
    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("User must be logged in");
        }
        return userService.getUserByUsername(principal.getName());
    }

    @GetMapping("/book/{id}")
    public String bookDetails(@PathVariable String id, Model model, Principal principal) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);

        // Add userId for authenticated users
        if (principal != null) {
            User user = getCurrentUser(principal);
            model.addAttribute("userId", user.getId());
        }

        return "book-details";
    }
}
