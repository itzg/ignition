package me.itzg.ignition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Geoff Bourne
 * @since 2/28/2015
 */
@SpringBootApplication
@ComponentScan
public class IgnitionApp {
    public static void main(String[] args) {
        SpringApplication.run(IgnitionApp.class, args);
    }

}
