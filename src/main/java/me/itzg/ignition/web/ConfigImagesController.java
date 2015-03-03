package me.itzg.ignition.web;

import com.google.common.base.Preconditions;
import me.itzg.ignition.services.ImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Geoff Bourne
 * @since 3/1/2015
 */
@RestController
@RequestMapping("/config/images")
public class ConfigImagesController {

    private static final String[] SUPPORTED_DISTRIBUTIONS = new String[]{"coreos"};
    private static final Set<String> COREOS_CHANNELS = new HashSet<>(Arrays.asList("stable", "beta", "alpha"));

    @Autowired
    private ImagesService imagesService;

    @RequestMapping(method = RequestMethod.GET)
    public String[] getSupportedDistributions() {
        return SUPPORTED_DISTRIBUTIONS;
    }

    @RequestMapping(value="/coreos", method = RequestMethod.GET)
    public Collection<String> getCoreOSChannels() {
        return COREOS_CHANNELS;
    }

    @RequestMapping(value="/coreos/{channel}", method = RequestMethod.POST)
    public void pullImagesCoreOS(
            @PathVariable String channel) {
        Preconditions.checkArgument(COREOS_CHANNELS.contains(channel), "Invalid CoreOS channel: "+channel);
        imagesService.pullImageCoreOS(channel);
    }
}
