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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

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
