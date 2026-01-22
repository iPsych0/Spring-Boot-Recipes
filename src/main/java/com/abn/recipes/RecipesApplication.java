package com.abn.recipes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class RecipesApplication {

	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(RecipesApplication.class, args);
	}


	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {

		System.out.println("\n------------------------------------------------------------");
		System.out.println("   Application started successfully! 🚀");
		System.out.println("   Swagger UI is available at:");
		System.out.printf("   👉 http://localhost:%s/swagger-ui.html\n", env.getProperty("server.port"));
		System.out.println("------------------------------------------------------------\n");
	}


}
