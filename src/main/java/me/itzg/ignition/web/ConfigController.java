package me.itzg.ignition.web;

import me.itzg.ignition.ConfigStatus;
import me.itzg.ignition.services.ConfigService;
import mousio.etcd4j.responses.EtcdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Geoff Bourne
 * @since 3/1/2015
 */
@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @RequestMapping(method = RequestMethod.GET)
    public ConfigStatus getConfigStatus() throws EtcdException, TimeoutException, IOException {
        return configService.getConfigStatus();
    }
}
