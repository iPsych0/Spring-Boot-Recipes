package com.abn.recipes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class RecipesApplication {

    private static final Logger log = LoggerFactory.getLogger(RecipesApplication.class);

    private final Environment env;

    public RecipesApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
        SpringApplication.run(RecipesApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        var port = env.getProperty("server.port", "8080");
        log.info("Application started successfully!");
        log.info("Swagger UI available at: http://localhost:{}/swagger-ui.html", port);
    }
}
