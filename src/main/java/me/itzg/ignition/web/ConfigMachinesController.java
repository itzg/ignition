package me.itzg.ignition.web;

import me.itzg.ignition.services.MachinesService;
import mousio.etcd4j.responses.EtcdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Geoff Bourne
 * @since 3/1/2015
 */
@RestController
@RequestMapping("/config/machines")
public class ConfigMachinesController {

    @Autowired
    private MachinesService machinesService;

    @RequestMapping(value="/default/{node}", method = RequestMethod.GET)
    public String getDefaultNode(@PathVariable String node) throws EtcdException, TimeoutException, IOException {
        return machinesService.getDefaultContent(node);
    }

    @RequestMapping(value="/default/{node}", method = RequestMethod.PUT)
    public String putDefaultNode(@PathVariable String node, @RequestBody String content) throws EtcdException, TimeoutException, IOException {
        String oldContent = null;
        try {
            oldContent = machinesService.getDefaultContent(node);
        } catch (FileNotFoundException e) {
            // normal when no previous content
        }

        machinesService.setDefaultContent(node, content);

        return oldContent;
    }
}
