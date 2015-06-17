package me.itzg.ignition.services;

import com.google.common.collect.ImmutableMap;
import me.itzg.ignition.ExtractResponseToFile;
import me.itzg.ignition.IgnitionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static me.itzg.ignition.IgnitionConstants.PATH_JOINER;

/**
 * @author Geoff Bourne
 * @since 3/1/2015
 */
@Service
public class ImagesService {
    private static Logger LOG = LoggerFactory.getLogger(ImagesService.class);
    public static final String VMLINUZ = "coreos_production_pxe.vmlinuz";
    public static final String INITRD = "coreos_production_pxe_image.cpio.gz";

    @Autowired
    @Qualifier("imagesBaseDirectory")
    private File imagesBaseDir;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private ImagesProperties imagesProperties;


    @PostConstruct
    public void init() {
        taskScheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                doDownloads();
            }
        }, TimeUnit.MILLISECONDS.convert(imagesProperties.getCheckInterval(), TimeUnit.MINUTES));
    }

    protected void doDownloads() {
        final Path imagesBasePath = imagesBaseDir.toPath();
        final Path coreOSPath = imagesBasePath.resolve(IgnitionConstants.IMAGES_DIR_COREOS);

        try {
            Files.createDirectories(coreOSPath);
        } catch (IOException e) {
            LOG.warn("Unable to create directory", e);
            return;
        }

        for (String channel : imagesProperties.getCoreOS().getChannels()) {
            downloadService.download(imagesProperties.getCoreOS().getBaseUrl() + "/{file}",
                    coreOSPath.resolve(channel).resolve(VMLINUZ),
                    ImmutableMap.<String, Object>builder()
                            .put("channel", channel)
                            .put("file", VMLINUZ)
                            .put("arch", IgnitionConstants.ARCH_AMD64)
                            .build());

            downloadService.download(imagesProperties.getCoreOS().getBaseUrl() + "/{file}",
                    coreOSPath.resolve(channel).resolve(INITRD),
                    ImmutableMap.<String, Object>builder()
                            .put("channel", channel)
                            .put("file", INITRD)
                            .put("arch", IgnitionConstants.ARCH_AMD64)
                            .build());
        }
    }
}
