package me.itzg.ignition.common;

/**
 * @author Geoff Bourne
 * @since 6/20/2015
 */
public class DoesNotExistException extends IgnitionException {
    public DoesNotExistException() {
    }

    public DoesNotExistException(String message) {
        super(message);
    }
}
