package org.amazinbookstore.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class User {

    @Getter
    @Setter
    @Id
    private String id;

    @Getter
    @Setter
    @NotBlank(message = "Username is required")
    private String username;

    @Getter
    @Setter
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Getter
    @Setter
    @NotBlank(message = "Password is required")
    private String password;

    @Getter
    @Setter
    private UserRole role = UserRole.CUSTOMER;

    // List of book IDs that the user has purchased (for recommendations)
    private List<String> purchasedBookIds = new ArrayList<>();

    public enum UserRole {
        CUSTOMER,
        OWNER
    }

    public User() {
    }

    public List<String> getPurchasedBookIds() {
        return new ArrayList<>(purchasedBookIds);
    }

    public void setPurchasedBookIds(List<String> purchasedBookIds) {
        this.purchasedBookIds = purchasedBookIds;
    }
}
