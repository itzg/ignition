package me.itzg.ignition.common;

import org.junit.Test;

import java.net.InetAddress;

import static org.junit.Assert.*;

/**
 * @author Geoff Bourne
 * @since 6/19/2015
 */
public class AddressUtilsTest {

    @Test
    public void testApplyIndex() throws Exception {
        byte[] masked = new byte[]{1,2,3,0};

        final byte[] result = AddressUtils.applyIndex(masked, 5);
        assertEquals(1, result[0]);
        assertEquals(2, result[1]);
        assertEquals(3, result[2]);
        assertEquals(5, result[3]);
    }

    @Test
    public void testApplyLargerIndex() throws Exception {
        byte[] masked = new byte[]{1,2,3,0};

        final byte[] result = AddressUtils.applyIndex(masked, 50);
        assertEquals(1, result[0]);
        assertEquals(2, result[1]);
        assertEquals(3, result[2]);
        assertEquals(50, result[3]);
    }

    @Test
    public void testMask() throws Exception {
        final byte[] original = InetAddress.getByName("1.2.3.4").getAddress();

        final byte[] masked = AddressUtils.mask(original, 24);
        assertEquals(1, masked[0]);
        assertEquals(2, masked[1]);
        assertEquals(3, masked[2]);
        assertEquals(0, masked[3]);
    }

    @Test
    public void testConvertPrefixToMask() throws Exception {
        assertEquals("255.255.255.0", AddressUtils.convertToSubnetMask(24));
        assertEquals("255.255.254.0", AddressUtils.convertToSubnetMask(23));
        assertEquals("240.0.0.0", AddressUtils.convertToSubnetMask(4));

    }
}