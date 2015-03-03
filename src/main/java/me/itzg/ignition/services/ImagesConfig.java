package me.itzg.ignition.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.validation.constraints.NotNull;
import java.io.File;

/**
 * @author Geoff Bourne
 * @since 2/28/2015
 */
@Configuration
@ConfigurationProperties(prefix = "images")
public class ImagesConfig extends WebMvcConfigurerAdapter {

    @NotNull
    private File baseDirectory;

    @Bean
    public File imagesBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDir(String dir) {
        File asFile = new File(dir);
        if (!asFile.isDirectory()) {
            throw new IllegalArgumentException("Not a valid directory: " + dir);
        }

        baseDirectory = asFile;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations(baseDirectory.toURI().toString());
    }
}
