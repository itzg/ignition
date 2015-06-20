package me.itzg.ignition.common;

/**
 * Indicates that the creation of something conflicted with an already existing instance with the same
 * identifier. Refer to the message for details.
 *
 * @author Geoff Bourne
 * @since 6/17/2015
 */
public class AlreadyExistsException extends IgnitionException {
    public AlreadyExistsException(String message) {
        super(message);
    }

}
