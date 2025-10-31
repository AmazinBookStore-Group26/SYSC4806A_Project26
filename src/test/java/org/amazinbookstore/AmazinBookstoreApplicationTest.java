package org.amazinbookstore;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Basic unit test for AmazinBookstoreApplication
 * Tests that the application class can be instantiated
 */
class AmazinBookstoreApplicationTest {

    @Test
    void testApplicationClassExists() {
        AmazinBookstoreApplication application = new AmazinBookstoreApplication();
        assertNotNull(application, "Application instance should not be null");
    }

    @Test
    void testMainMethodExists() throws NoSuchMethodException {
        // Verify that the main method exists with the correct signature
        var mainMethod = AmazinBookstoreApplication.class.getMethod("main", String[].class);
        assertNotNull(mainMethod, "Main method should exist");
    }
}
