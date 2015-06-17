package me.itzg.ignition.services;

import mousio.etcd4j.EtcdClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.List;

/**
 * @author Geoff Bourne
 * @since 2/28/2015
 */
@Configuration
public class EtcdConfig {

    @Bean
    public EtcdProperties etcdProperties() {
        return new EtcdProperties();
    }

    @Bean
    public EtcdClient etcdClient() {
        return new EtcdClient(etcdProperties().getMachines());
    }
}
