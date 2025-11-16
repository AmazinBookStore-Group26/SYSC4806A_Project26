package org.amazinbookstore;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Basic unit test for AmazinBookstoreApplication
 * Tests that the application class can be instantiated
 */
class AmazinBookstoreApplicationTest {

    /**
     * Tests that the AmazinBookstoreApplication class can be instantiated.
     * Verifies that the application instance is not null.
     */
    @Test
    void testApplicationClassExists() {
        AmazinBookstoreApplication application = new AmazinBookstoreApplication();
        assertNotNull(application, "Application instance should not be null");
    }

    /**
     * Tests that the main method exists with the correct signature.
     * Verifies that the entry point of the application is properly defined.
     *
     * @throws NoSuchMethodException if the main method is not found
     */
    @Test
    void testMainMethodExists() throws NoSuchMethodException {
        // Verify that the main method exists with the correct signature
        var mainMethod = AmazinBookstoreApplication.class.getMethod("main", String[].class);
        assertNotNull(mainMethod, "Main method should exist");
    }
}
