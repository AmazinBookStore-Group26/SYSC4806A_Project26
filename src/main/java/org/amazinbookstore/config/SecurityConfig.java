package org.amazinbookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Provides a BCrypt-based password encoder for hashing user passwords.
     * BCrypt is a strong hashing function designed specifically for secure password storage
     * and includes built-in salting and computational cost control.
     *
     * @return a {@link PasswordEncoder} using BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the application's {@link AuthenticationManager}.
     * This allows authentication logic to be reused elsewhere in the application,
     * such as during custom login or user validation flows.
     *
     * @param authConfig the Spring Security authentication configuration
     * @return the shared {@link AuthenticationManager} instance
     * @throws Exception if the authentication manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Defines the main HTTP security rules for the application.
     *     Publicly accessible pages such as login, registration, CSS, and JS.
     *     Public GET access for browsing available books.
     *     Restricted POST/PUT/DELETE operations on books to users with the OWNER role.
     *     Role-based access to admin views.
     *     Form login and logout behavior.
     *     CSRF disabled for API routes to allow non-browser clients.
     *
     * @param http the {@link HttpSecurity} builder for configuring security behavior
     * @return the constructed {@link SecurityFilterChain}
     * @throws Exception if the filter chain cannot be built
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // public pages, anyone can access
                        .requestMatchers("/", "/register", "/login", "/css/**", "/js/**").permitAll()
                        // public GET for browsing books
                        .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()

                        // admin/owner only endpoints for managing books
                        .requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("OWNER")
                        .requestMatchers("/admin/**").hasRole("OWNER")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                );

        return http.build();
    }
}
