package org.education.firstwebproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@ConfigurationPropertiesScan("org.education.firstwebproject")
public class FirstWebProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(FirstWebProjectApplication.class, args);
    }
}
