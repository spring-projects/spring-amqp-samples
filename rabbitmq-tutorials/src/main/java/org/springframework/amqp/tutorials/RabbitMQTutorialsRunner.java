package org.springframework.amqp.tutorials;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;

public class RabbitMQTutorialsRunner implements CommandLineRunner {

	@Value("${tutorial.client.duration:0}")
	private int duration;

	@Autowired
	private ConfigurableApplicationContext ctx;

	@Override
	public void run(String... arg0) throws Exception {
		ctx.start();
		Thread.sleep(duration);
		ctx.close();
	}
}
