package me.itzg.ignition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Geoff Bourne
 * @since 2/28/2015
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class IgnitionApp {
    public static void main(String[] args) {
        SpringApplication.run(IgnitionApp.class, args);
    }

}
