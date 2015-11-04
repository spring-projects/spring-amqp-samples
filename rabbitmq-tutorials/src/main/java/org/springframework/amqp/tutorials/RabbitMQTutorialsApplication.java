package org.springframework.amqp.tutorials;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class RabbitMQTutorialsApplication {
	
	@Profile("usage_message")
	@Bean
	public CommandLineRunner usage() {
		return new CommandLineRunner() {
			@Override
			public void run(String... arg0) throws Exception {
				System.out.println("This app uses Spring Profiles to control its behavior.\n");
				System.out.println("Sample usage: java -jar rabbit-tutorials.jar --spring.profiles.active=tut{1-2}{sender|receiver}");
			}
		};
	}
	
	@Profile("!usage_message")
	@Bean
	public CommandLineRunner tutorial() {
		return new RabbitMQTutorialsRunner();
	}

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RabbitMQTutorialsApplication.class, args);
    }
}
