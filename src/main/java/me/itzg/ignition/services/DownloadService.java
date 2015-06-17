package me.itzg.ignition.services;

import me.itzg.ignition.ExtractResponseToFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author Geoff Bourne
 * @since 6/16/2015
 */
@Service
public class DownloadService {
    private static Logger LOG = LoggerFactory.getLogger(DownloadService.class);

    private RestTemplate restTemplate = new RestTemplate();

    @Async("downloads")
    public void download(String uriString, Path destination, Map<String,?> uriVariables) {
        final URI uri = UriComponentsBuilder.fromUriString(uriString)
                .buildAndExpand(uriVariables)
                .toUri();
        LOG.debug("Checking for download of {}", uri);

        final HttpHeaders httpHeaders = restTemplate.headForHeaders(uri);

        HttpHeaders previousHeaders = null;

        final Path headersFile = destination.resolveSibling(destination.getName(destination.getNameCount() - 1) + ".headers");
        try {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(headersFile.toFile()))) {
                previousHeaders = (HttpHeaders) in.readObject();
            }
        } catch (FileNotFoundException e){
            LOG.info("Headers not previously observed for {}", uri);
        } catch (IOException e) {
            LOG.warn("Issue while checking observed headers from {}", headersFile, e);
        } catch (ClassNotFoundException e) {
            LOG.warn("Invalid content in {}", headersFile, e);
        }

        if (previousHeaders != null) {
            if (previousHeaders.get(HttpHeaders.ETAG).equals(httpHeaders.get(HttpHeaders.ETAG))) {
                LOG.debug("Existing ETag of {} matched", destination);
                return;
            }

            LOG.debug("Previously observed, but mismatching headers");
            try {
                Files.delete(headersFile);
            } catch (IOException e) {
                LOG.warn("Trying to delete mismatching headers file", e);
            }
        }

        LOG.info("Downloading {} to {}", uri, destination);
        try {
            Files.createDirectories(destination.getParent());
        } catch (IOException e) {
            LOG.warn("Unable to create containing directory", e);
            return;
        }

        restTemplate.execute(uri, HttpMethod.GET, null, new ExtractResponseToFile(destination));

        try {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(headersFile.toFile()))) {
                out.writeObject(httpHeaders);
            }
        } catch (IOException e) {
            LOG.warn("Trying to write observed headers into {}", headersFile, e);
        }

        LOG.debug("Finished downloading {} to {}", uri, destination);
    }
}
