package io.codefresh.gradleexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		setEnvironmentalVariables();
		SpringApplication.run(Application.class, args);
	}

	private static void setEnvironmentalVariables(){
		String serverAddress = System.getenv("SERVER_ADDRESS");
		if (serverAddress != null) {
			String[] parts = serverAddress.split(":");
			if (parts.length == 2) {
				System.setProperty("server.address", parts[0]);
				System.setProperty("server.port", parts[1]);
			}
		}
	}
}
