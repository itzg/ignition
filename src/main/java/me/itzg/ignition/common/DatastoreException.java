package me.itzg.ignition.common;

/**
 * Indicates an internal issue occurred contacting of the datastores.
 *
 * @author Geoff Bourne
 * @since 6/17/2015
 */
public class DatastoreException extends Exception {
    public DatastoreException() {
    }

    public DatastoreException(String message) {
        super(message);
    }

    public DatastoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatastoreException(Throwable cause) {
        super(cause);
    }

    public DatastoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
