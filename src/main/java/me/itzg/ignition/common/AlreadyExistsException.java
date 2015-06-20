package me.itzg.ignition.common;

/**
 * Indicates that the creation of something conflicted with an already existing instance with the same
 * identifier. Refer to the message for details.
 *
 * @author Geoff Bourne
 * @since 6/17/2015
 */
public class AlreadyExistsException extends Exception {
    public AlreadyExistsException(String message) {
        super(message);
    }

    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
