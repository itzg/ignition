package me.itzg.ignition.web;

import me.itzg.ignition.common.AlreadyExistsException;
import me.itzg.ignition.common.DatastoreException;
import me.itzg.ignition.common.IgnitionException;
import me.itzg.ignition.common.IpPoolDeclaration;
import me.itzg.ignition.common.NetAllocation;
import me.itzg.ignition.etcd.EtcdException;
import me.itzg.ignition.services.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Geoff Bourne
 * @since 6/17/2015
 */
@RestController
@RequestMapping("/config/net")
public class ConfigNetController {

    @Autowired
    private ConfigService configService;

    @RequestMapping(value = "ip-pool", method = RequestMethod.POST)
    public void declareIpPool(@Valid @RequestBody IpPoolDeclaration ipPoolDeclaration) throws AlreadyExistsException, DatastoreException, IOException, EtcdException {
        configService.declareIpPool(ipPoolDeclaration);
    }

    @RequestMapping(value="allocate", method = RequestMethod.POST)
    public NetAllocation allocate(@RequestParam("node") UUID nodeId, @RequestParam("pool") String ipPoolName)
            throws EtcdException, IgnitionException, IOException {
        return configService.allocateFromIpPool(ipPoolName, nodeId);
    }

    @RequestMapping(value="release", method = RequestMethod.POST)
    public void release(@RequestParam("node") UUID nodeId,
                        @RequestParam("pool") String ipPoolName, @RequestParam("address") String ipAddress)
            throws EtcdException, IgnitionException, IOException {
        configService.releaseBackToIpPool(ipPoolName, nodeId, ipAddress);
    }
}
