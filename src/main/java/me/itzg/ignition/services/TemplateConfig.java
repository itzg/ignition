package me.itzg.ignition.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Geoff Bourne
 * @since 2/28/2015
 */
@Configuration
public class TemplateConfig {

    @Bean
    public freemarker.template.Configuration freemarkerConfig(EtcdTemplateLoader etcdTemplateLoader) {
        freemarker.template.Configuration config = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_21);
        config.setTemplateLoader(etcdTemplateLoader);
        return config;
    }
}
