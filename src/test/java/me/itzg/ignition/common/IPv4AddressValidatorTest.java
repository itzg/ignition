package me.itzg.ignition.common;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Geoff Bourne
 * @since 6/17/2015
 */
public class IPv4AddressValidatorTest {

    private IPv4AddressValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new IPv4AddressValidator();
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(validator.isValid("1.2.3.4", null));
        assertFalse(validator.isValid("example.com", null));
        assertFalse(validator.isValid("1.5", null));
    }
}