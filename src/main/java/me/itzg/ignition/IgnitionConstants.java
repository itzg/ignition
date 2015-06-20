package me.itzg.ignition;

import com.google.common.base.Joiner;

/**
 * @author Geoff Bourne
 * @since 2/28/2015
 */
public class IgnitionConstants {
    public static final String ETCD_BASE = "/ignition";

    public static final String ETCD_MACHINES = "machines";
    public static final String ETCD_NET = "net";
    public static final String ETCD_IP_POOLS = "ip-pools";
    public static final String ETCD_NODES = "nodes";
    public static final String ETCD_METADATA = "_metadata";
    public static final String ETCD_SSHKEY_NODE = "sshkey";

    public static final String ETCD_DEFAULT = "default";

    public static final int ETCD_ERR_KEY_NOT_FOUND = 100;
    public static final String TEMPLATE_SCHEME_MACHINE = "machine";
    public static final String ARCH_AMD64 = "amd64";
    public static final String IMAGES_DIR_COREOS = "coreos";
}
