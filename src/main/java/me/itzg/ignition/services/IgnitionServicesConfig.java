package me.itzg.ignition.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.awt.*;
import java.io.File;

/**
 * @author Geoff Bourne
 * @since 6/16/2015
 */
@Configuration
public class IgnitionServicesConfig {
    @Autowired
    private ImagesProperties imagesProperties;

    @Bean
    public File imagesBaseDirectory() {
        return new File(imagesProperties.getBaseDirectory());
    }

    @Bean
    public TaskExecutor downloads() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(imagesProperties.getDownloadConcurrency());
        executor.setCorePoolSize(imagesProperties.getDownloadConcurrency());
        return executor;
    }

    @Bean
    public TaskScheduler scheduler() {
        final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        return scheduler;
    }

}
