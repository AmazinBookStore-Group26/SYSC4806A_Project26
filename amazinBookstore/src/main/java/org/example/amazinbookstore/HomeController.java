package org.example.amazinbookstore;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class HomeController {

    // Create an endpoint for the root URL ("/")
    @GetMapping("/")
    public String hello() {
        // Tells spring to find hello.html
        return "hello";
    }
}