package org.amazinbookstore.dto;

import org.amazinbookstore.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {

    /**
     * The username chosen by the user.
     * Must not be blank.
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * The user's first name.
     * Must not be blank.
     */
    @NotBlank(message = "First name is required")
    private String firstName;

    /**
     * The user's last name.
     * Must not be blank.
     */
    @NotBlank(message = "Last name is required")
    private String lastName;

    /**
     * Email address for the account.
     * Must be valid and not blank.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * Raw password provided during registration.
     * Validated using a custom {@link ValidPassword} annotation.
     */
    @NotBlank(message = "Password is required")
    @ValidPassword
    private String password;

    /**
     * The role assigned to the new account.
     * Defaults to CUSTOMER unless overridden.
     */
    private String role = "CUSTOMER";
}
