package me.itzg.ignition.web;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import me.itzg.ignition.IgnitionConstants;
import me.itzg.ignition.IgnitionProperties;
import me.itzg.ignition.TemplateContext;
import me.itzg.ignition.services.MachinesService;
import mousio.etcd4j.responses.EtcdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.TimeoutException;

/**
 * @author Geoff Bourne
 * @since 2/28/2015
 */
@RestController
public class AutoScopedController {
    private static Logger LOG = LoggerFactory.getLogger(AutoScopedController.class);

    @Autowired
    private Configuration freemarkerConfig;

    @Autowired
    private MachinesService machinesService;

    @Autowired
    private IgnitionProperties ignitionProperties;

    @RequestMapping(value = "/{file}", method = RequestMethod.GET)
    public String getFileAutoScoped(HttpServletRequest request, @PathVariable String file,
                                  @RequestParam(value="role", required = false) String role) throws IOException, TimeoutException, EtcdException, TemplateException {
        final String remoteHost = request.getRemoteHost();

        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance()
                .scheme(IgnitionConstants.TEMPLATE_SCHEME_MACHINE)
                .path(file)
                .queryParam(IgnitionConstants.ETCD_BY_IP, remoteHost);

        if (role != null) {
            uriBuilder.queryParam(IgnitionConstants.ETCD_BY_ROLE, role);
        }

        final Template template = freemarkerConfig.getTemplate(uriBuilder.build().toUriString());

        final TemplateContext context = new TemplateContext();
        context.setName(machinesService.getOrCreateMachineName(remoteHost));
        context.setRole(machinesService.getMachineRole(remoteHost, role));
        try {
            context.setSshKey(machinesService.getMachineSshKey(remoteHost, role));
        } catch (FileNotFoundException e) {
            LOG.debug("No sshkey available for {}", context);
        }
        context.setPublic_ipv4(remoteHost); //TODO, allow for configured
        context.setPrivate_ipv4(remoteHost);
        context.setIgnitionBaseUrl(ignitionProperties.getPublishAs());

        LOG.info("Handling auto-scoping of the file {} for {}", file, context);

        final StringWriter response = new StringWriter();
        template.process(context, response);

        return response.toString();
    }
}
