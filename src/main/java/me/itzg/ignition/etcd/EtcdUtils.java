package me.itzg.ignition.etcd;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import me.itzg.ignition.etcd.keys.Node;

import java.util.List;

/**
 * @author Geoff Bourne
 * @since 6/20/2015
 */
public class EtcdUtils {
    private static final Joiner pathJoiner = Joiner.on("/");
    private static final Splitter pathSplitter = Splitter.on("/").omitEmptyStrings();

    /**
     * Extracts just the name part of a node's key-path
     *
     * @param node
     * @return
     */
    public static String extractNameFromNode(Node node) {
        final String key = node.getKey();
        final List<String> parts = pathSplitter.splitToList(key);
        return parts.get(parts.size() - 1);
    }

    public static String[] extractParentPath(Node node) {
        final String key = node.getKey();
        final List<String> parts = pathSplitter.splitToList(key);

        final String[] ret = new String[parts.size() - 1];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = parts.get(i);
        }
        return ret;
    }

    /**
     * Simple wrapper to throw {@link IllegalStateException} when given node is null which was due
     * to not existing.
     * @param node
     * @return the value from the node
     * @throws IllegalStateException when node is null, i.e. doesn't exist
     */
    public static String extractValue(Node node) {
        if (node == null) {
            throw new IllegalStateException("Node did not exist");
        }
        return node.getValue();
    }

    public static int extractIntValue(Node node) {
        if (node == null) {
            throw new IllegalStateException("Node did not exist");
        }
        return Integer.parseInt(node.getValue());
    }
}
