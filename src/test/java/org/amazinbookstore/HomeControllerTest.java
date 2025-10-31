package org.amazinbookstore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit test for HomeController - temporary lightweight test without Spring context
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
