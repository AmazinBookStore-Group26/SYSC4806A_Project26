package org.amazinbookstore.exception;

/**
 * Exception thrown when an operation cannot be completed due to insufficient
 * inventory for a requested book.
 *
 * Typically used during order placement or cart updates when the requested
 * quantity exceeds available stock.
 */
public class InsufficientInventoryException extends RuntimeException {

    /**
     * Creates a new exception with the specified detail message.
     *
     * @param message explanation of the inventory constraint
     */
    public InsufficientInventoryException(String message) {
        super(message);
    }
}
