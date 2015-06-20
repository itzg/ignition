package me.itzg.ignition.common;

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
                byte mask = (byte) (0xf << (8-prefix));
                ret[i] = (byte) (incoming[i] & mask);
                prefix = 0;
            }
            else {
                ret[i] = 0;
            }
        }

        return ret;
    }
}
