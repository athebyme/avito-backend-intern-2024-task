package io.codefresh.gradleexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

@SpringBootApplication
public class GradleExampleApplication{

	public static void main(String[] args) {
		SpringApplication.run(GradleExampleApplication.class, args);
	}

}
