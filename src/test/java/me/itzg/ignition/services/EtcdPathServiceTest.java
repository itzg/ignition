package me.itzg.ignition.services;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Geoff Bourne
 * @since 6/17/2015
 */
public class EtcdPathServiceTest {

    @Test
    public void testBuild() throws Exception {
        EtcdPathService service = new EtcdPathService();
        final String path = service.build("top", "lower");
        assertEquals("/ignition/top/lower", path);
    }
}