package me.itzg.ignition.services;

import me.itzg.ignition.etcd.keys.Node;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Geoff Bourne
 * @since 6/17/2015
 */
public class EtcdServiceTest {

    private EtcdService etcdService;

    @Before
    public void setUp() throws Exception {


        etcdService = new EtcdService();
    }

    @Test
    public void testBuild() throws Exception {
        EtcdService service = etcdService;
        final String path = service.build("top", "lower");
        assertEquals("/ignition/top/lower", path);
    }
}