package me.itzg.ignition.services;

import me.itzg.ignition.ExtractResponseToFile;
import me.itzg.ignition.IgnitionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static me.itzg.ignition.IgnitionConstants.PATH_JOINER;

/**
 * @author Geoff Bourne
 * @since 3/1/2015
 */
@Service
public class ImagesService {
    private static Logger LOG = LoggerFactory.getLogger(ImagesService.class);

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    @Qualifier("imagesBaseDirectory")
    private File imagesBaseDir;

    @Async
    public void pullImageCoreOS(String channel) {
        LOG.info("Starting pull of CoreOS {} channel", channel);
        String baseurl = "http://{channel}.release.core-os.net/{arch}-usr/current/{file}";
        String vmlinuz = "coreos_production_pxe.vmlinuz";
        String initrd = "coreos_production_pxe_image.cpio.gz";

        Map<String, String> params = new HashMap<>();
        params.put("channel", channel);
        params.put("arch", IgnitionConstants.ARCH_AMD64);

        final Path outDir = new File(imagesBaseDir, PATH_JOINER.join(IgnitionConstants.IMAGES_DIR_COREOS, params.get("arch"), channel)).toPath();
        try {
            Files.createDirectories(outDir);
            download(baseurl, vmlinuz, params, outDir);
            download(baseurl, initrd, params, outDir);
        } catch (IOException e) {
            LOG.error("Trying to create output directory", e);
        }


        LOG.info("Finished pull of CoreOS {} channel", channel);
    }

    private void download(String baseurl, String file, Map<String, String> params, Path outDir) throws IOException {
        params.put("file", file);
        final HttpHeaders httpHeaders = restTemplate.headForHeaders(baseurl, params);
        final long contentLength = httpHeaders.getContentLength();
        final long lastModified = httpHeaders.getLastModified();

        LOG.debug("Downloading {} ({} bytes) using {}", file, contentLength, params);
        final Path outFile = outDir.resolve(file);

        if (Files.exists(outFile)
                && Files.size(outFile) == contentLength
                && Files.getLastModifiedTime(outFile).toMillis() == lastModified) {
            LOG.info("{} is already up to date", outFile);
            return;
        }

        restTemplate.execute(baseurl, HttpMethod.GET, null, new ExtractResponseToFile(outFile), params);
        Files.setLastModifiedTime(outFile, FileTime.fromMillis(lastModified));
    }
}
