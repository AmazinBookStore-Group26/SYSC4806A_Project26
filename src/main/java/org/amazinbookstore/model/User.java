package org.amazinbookstore.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private UserRole role = UserRole.CUSTOMER;

    // List of book IDs that the user has purchased (for recommendations)
    private List<String> purchasedBookIds = new ArrayList<>();

    public enum UserRole {
        CUSTOMER,
        OWNER
    }

    // Constructors
    public User() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public List<String> getPurchasedBookIds() {
        return purchasedBookIds;
    }

    public void setPurchasedBookIds(List<String> purchasedBookIds) {
        this.purchasedBookIds = purchasedBookIds;
    }
}
