package org.codejudge.sb;

import lombok.extern.slf4j.Slf4j;
import org.codejudge.sb.service.api.AppService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"org.codejudge.sb"})
@Slf4j
public class Application {

    public static void main(String[] args) {
        log.info("Starting Application...");
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        AppService appService = context.getBean(AppService.class);
        String filename = "input.txt";
    }
}
