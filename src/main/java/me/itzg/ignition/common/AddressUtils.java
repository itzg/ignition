package me.itzg.ignition.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Geoff Bourne
 * @since 6/19/2015
 */
public class AddressUtils {
    public static byte[] applyIndex(byte[] masked, int index) {
        byte[] ret = new byte[masked.length];

        for (int offset = masked.length-1; offset >= 0; --offset) {
            ret[offset] = (byte) (masked[offset] | (index & 0xff));
            index >>= 8;
        }

        return ret;
    }

    public static byte[] mask(byte[] incoming, int prefix) {
        byte[] ret = new byte[incoming.length];

        for (int i = 0; i < incoming.length; ++i) {
            if (prefix >= 8) {
                ret[i] = incoming[i];
                prefix -= 8;
            }
            else if (prefix > 0) {
                byte mask = (byte) (0xff << (8-prefix));
                ret[i] = (byte) (incoming[i] & mask);
                prefix = 0;
            }
            else {
                ret[i] = 0;
            }
        }

        return ret;
    }

    /**
     * NOTE: only works with IPv4 addresses
     * @param prefix
     * @return
     */
    public static String convertToSubnetMask(int prefix) {
        final int[] asUnsignedBytes = new int[4];
        for (int i = 0; i < 4; i++) {
            if (prefix > 8) {
                asUnsignedBytes[i] = 0xff;
                prefix -= 8;
            }
            else if (prefix > 0) {
                int mask = (0xff << (8-prefix)) & 0xff;
                asUnsignedBytes[i] = mask;
                prefix = 0;
            }
            else {
                asUnsignedBytes[i] = 0;
            }
        }

        return String.format("%d.%d.%d.%d",
                asUnsignedBytes[0],asUnsignedBytes[1],asUnsignedBytes[2],asUnsignedBytes[3]);
    }
}
