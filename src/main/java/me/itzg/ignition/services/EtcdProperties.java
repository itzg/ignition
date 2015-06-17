package me.itzg.ignition.services;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * @author Geoff Bourne
 * @since 6/16/2015
 */
@ConfigurationProperties(prefix = "ignition.etcd")
public class EtcdProperties {
    private URI[] machines;

    public URI[] getMachines() {
        return machines;
    }

    public void setMachines(URI[] machines) {
        this.machines = machines;
    }
}
