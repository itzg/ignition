package me.itzg.ignition.web;

import me.itzg.ignition.common.AlreadyExistsException;
import me.itzg.ignition.common.DatastoreException;
import me.itzg.ignition.common.IpPoolDeclaration;
import me.itzg.ignition.etcd.EtcdException;
import me.itzg.ignition.services.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

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
}
