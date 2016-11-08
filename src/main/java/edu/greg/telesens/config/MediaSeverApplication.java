package edu.greg.telesens.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "edu.greg.telesens.server")
public class MediaSeverApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediaSeverApplication.class, args);
    }
}
