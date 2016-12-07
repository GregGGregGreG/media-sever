package edu.greg.telesens.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;

@SpringBootApplication
@ComponentScan(value = "edu.greg.telesens.server")
public class MediaSeverApplication {


    public static void main(String[] args) {
        SpringApplication.run(MediaSeverApplication.class, args);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    Logger log(InjectionPoint point) {
        return LoggerFactory.getLogger(point.getMember().getDeclaringClass());
    }
}
