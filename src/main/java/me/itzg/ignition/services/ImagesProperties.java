package me.itzg.ignition.services;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.File;
import java.util.List;

/**
 * @author Geoff Bourne
 * @since 6/16/2015
 */
@Component
@ConfigurationProperties(prefix = "ignition.images")
public class ImagesProperties {
    @NotNull
    private String baseDirectory;

    @Min(value = 1)
    private int downloadConcurrency = 2;

    @Min(value = 1)
    private int checkInterval = 4*60;

    @NotNull
    private CoreOSProperties coreOS;

    public void setBaseDirectory(String dir) {
        baseDirectory = dir;
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public int getDownloadConcurrency() {
        return downloadConcurrency;
    }

    public void setDownloadConcurrency(int downloadConcurrency) {
        this.downloadConcurrency = downloadConcurrency;
    }

    public int getCheckInterval() {
        return checkInterval;
    }

    /**
     *
     * @param checkInterval in minutes
     */
    public void setCheckInterval(int checkInterval) {
        this.checkInterval = checkInterval;
    }

    public CoreOSProperties getCoreOS() {
        return coreOS;
    }

    public void setCoreOS(CoreOSProperties coreOS) {
        this.coreOS = coreOS;
    }

    public static class CoreOSProperties {
        @Size(min = 1)
        private List<String> channels;

        private String baseUrl = "http://{channel}.release.core-os.net/{arch}-usr/current";

        public List<String> getChannels() {
            return channels;
        }

        public void setChannels(List<String> channels) {
            this.channels = channels;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }
}
