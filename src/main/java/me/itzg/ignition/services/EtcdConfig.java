package me.itzg.ignition.services;

import mousio.etcd4j.EtcdClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

/**
 * @author Geoff Bourne
 * @since 2/28/2015
 */
@Configuration
@ConfigurationProperties(prefix = "etcd")
public class EtcdConfig {
    private static Logger LOG = LoggerFactory.getLogger(EtcdConfig.class);

    private URI[] baseUris;

    @Bean
    public EtcdClient etcdClient() {
        return new EtcdClient(baseUris);
    }

    public void setUris(String blobbedUris) {
        final String[] splits = blobbedUris.split(",");
        baseUris = new URI[splits.length];

        for (int i = 0; i < splits.length; i++) {
            baseUris[i] = URI.create(splits[i]);
        }

        LOG.info("baseUris = {}", baseUris);
    }
}
