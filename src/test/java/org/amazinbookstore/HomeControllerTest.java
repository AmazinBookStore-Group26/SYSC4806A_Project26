package org.amazinbookstore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit test for HomeController - lightweight test without Spring context
 * Perfect for CI/CD pipelines as it runs quickly
 */
class HomeControllerTest {

    private HomeController homeController;

    @BeforeEach
    void setUp() {
        homeController = new HomeController();
    }

    @Test
    void testHomeControllerInitialization() {
        assertNotNull(homeController, "HomeController should be instantiated");
    }

    @Test
    void testHelloEndpointReturnsCorrectView() {
        String viewName = homeController.hello();
        assertEquals("hello", viewName, "Hello endpoint should return 'hello' view name");
    }
}
