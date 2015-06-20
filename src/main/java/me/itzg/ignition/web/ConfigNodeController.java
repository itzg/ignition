package me.itzg.ignition.web;

import me.itzg.ignition.common.IgnitionException;
import me.itzg.ignition.common.NodeAssignment;
import me.itzg.ignition.etcd.EtcdException;
import me.itzg.ignition.services.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
 * @since 6/20/2015
 */
@RestController
@RequestMapping("/config/node")
public class ConfigNodeController {
    @Autowired
    private ConfigService configService;

    @RequestMapping(method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public void assignSubmit(@ModelAttribute @Valid NodeAssignment nodeAssignment)
            throws IOException, IgnitionException, EtcdException {

        configService.assignNode(nodeAssignment);

    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void assign(@RequestBody @Valid NodeAssignment nodeAssignment)
            throws IOException, IgnitionException, EtcdException {

        configService.assignNode(nodeAssignment);

    }

    @RequestMapping(value="{id}", method = RequestMethod.DELETE)
    public void teardown(@PathVariable("id") UUID nodeId) throws EtcdException, IgnitionException, IOException {
        configService.teardownNode(nodeId);
    }
}
