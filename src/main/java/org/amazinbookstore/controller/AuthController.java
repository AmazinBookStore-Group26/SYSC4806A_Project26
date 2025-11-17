package org.amazinbookstore.controller;

import org.amazinbookstore.dto.UserRegistrationDTO;
import org.amazinbookstore.model.User;
import org.amazinbookstore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Handles authentication-related endpoints including login and user registration.
 * <p>
 * Provides views for login and registration pages, validates user input,
 * and delegates creation of new user accounts to {@link UserService}.
 */
@Controller
public class AuthController {

    private final UserService userService;

    /**
     * Constructs the authentication controller with a required {@link UserService}.
     *
     * @param userService service responsible for user creation and retrieval
     */
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays the login page.
     *
     * @return the name of the Thymeleaf template for the login view
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * Displays the registration page and initializes the form with a new DTO.
     *
     * @param model the UI model used to pass attributes to the view layer
     * @return the registration page template
     */
    @GetMapping("/register")
    public String registrationPage(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        return "register";
    }

    /**
     * Handles user registration form submissions.
     *
     * @param registrationDTO form data submitted by the user
     * @param result binding and validation results
     * @param model the UI model for passing data back to the view layer
     * @return redirect to login page upon success, or reload registration page on error
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDTO registrationDTO,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            User user = new User();
            user.setUsername(registrationDTO.getUsername());
            user.setFirstName(registrationDTO.getFirstName());
            user.setLastName(registrationDTO.getLastName());
            user.setEmail(registrationDTO.getEmail());
            user.setPassword(registrationDTO.getPassword());

            // Convert role string to enum
            User.UserRole userRole = User.UserRole.valueOf(registrationDTO.getRole());
            user.setRole(userRole);

            userService.createUser(user);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
