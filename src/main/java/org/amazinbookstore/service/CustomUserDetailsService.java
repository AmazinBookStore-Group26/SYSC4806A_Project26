package org.amazinbookstore.service;

import org.amazinbookstore.model.User;
import org.amazinbookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom implementation of {@link UserDetailsService} used by Spring Security
 * to load user authentication details from the database.
 *
 * This service supports login using either a username or email address.
 * The returned {@link UserDetails} object includes the user’s credentials
 * and granted authorities based on their assigned role.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructs the service with a {@link UserRepository} dependency.
     *
     * @param userRepository repository used for user lookups
     */
    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by username or email for authentication.
     * <p>
     * Lookup order:
     *     Try username match
     *     If not found, try email match
     *     If still not found, throw {@link UsernameNotFoundException}
     *
     * @param usernameOrEmail the login identifier (username or email)
     * @return a Spring Security {@link UserDetails} instance
     * @throws UsernameNotFoundException if no matching user is found
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        // try to find user by username first, then by email
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail)));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                getAuthorities(user)
        );
    }

    /**
     * Converts the user's application role into Spring Security authorities.
     *
     * Roles are prefixed with ROLE_ as required by Spring Security.
     *
     * @param user the application user
     * @return a singleton collection containing the user’s granted authority
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }
}
