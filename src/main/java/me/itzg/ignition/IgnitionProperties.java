package me.itzg.ignition;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Geoff Bourne
 * @since 3/1/2015
 */
@Component
@ConfigurationProperties(prefix = "ignition")
public class IgnitionProperties {
    private String publishAs;

    public String getPublishAs() {
        return publishAs;
    }

    public void setPublishAs(String publishAs) {
        this.publishAs = publishAs;
    }
}
