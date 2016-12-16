package edu.greg.telesens.config;

import edu.greg.telesens.server.MediaStreamServiceImpl;
import edu.greg.telesens.server.session.SessionRegistry;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;

import java.util.Arrays;

@SpringBootApplication
@ComponentScan(value = "edu.greg.telesens.server")
public class MediaSeverApplication {

    @Autowired
    private Bus bus;

    @Autowired
    private SessionRegistry sessionRegistry;

    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(MediaSeverApplication.class, args);
        System.out.println("point");
    }

    @Bean
    public Server rsServer() {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setServiceBean(new MediaStreamServiceImpl(sessionRegistry));
        endpoint.setBus(bus);
        return endpoint.create();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    Logger log(InjectionPoint point) {
        return LoggerFactory.getLogger(point.getMember().getDeclaringClass());
    }
}
