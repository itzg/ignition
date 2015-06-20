package me.itzg.ignition.common;

/**
 * @author Geoff Bourne
 * @since 6/20/2015
 */
public class IgnitionException extends Exception {
    public IgnitionException() {
    }

    public IgnitionException(String message) {
        super(message);
    }

    public IgnitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public IgnitionException(Throwable cause) {
        super(cause);
    }
}
