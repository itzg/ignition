package me.itzg.ignition.etcd;

/**
 * @author Geoff Bourne
 * @since 6/19/2015
 */
public class EtcdException extends Exception {
    public EtcdException() {
    }

    public EtcdException(String message) {
        super(message);
    }

    public EtcdException(String message, Throwable cause) {
        super(message, cause);
    }

    public EtcdException(Throwable cause) {
        super(cause);
    }
}
