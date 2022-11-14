package com.mcla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.context.annotation.Configuration;


@Configuration
@SpringBootApplication(exclude = QuartzAutoConfiguration.class)
public class StartWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(StartWebApplication.class, args);
    }

}