package org.springframework.amqp.tutorials.tut6.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ClientApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext client = SpringApplication.run(ClientApplication.class, args);
		client.start();
		Thread.sleep(10000);
		client.close();
	}

	@Bean
	public DirectExchange exchange() {
		return new DirectExchange("tut.rpc");
	}

	@Bean
	public Sender sender() {
		return new Sender();
	}

	public static class Sender implements Lifecycle {

		private ExecutorService executor;

		@Autowired
		private RabbitTemplate template;

		@Autowired
		private DirectExchange exchange;

		@Override
		public boolean isRunning() {
			return this.executor != null && !this.executor.isShutdown();
		}

		@Override
		public void start() {
			this.executor = Executors.newSingleThreadExecutor();
			this.executor.execute(new Runnable() {

				@Override
				public void run() {
					int start = 0;
					while (true) {
						System.out.println(" [x] Requesting fib(" + start++ + ")");
						Integer response = (Integer) template.convertSendAndReceive(exchange.getName(), "rpc", start);
						System.out.println(" [.] Got '" + response + "'");
						try {
							Thread.sleep(1000);
						}
						catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							break;
						}
					}
				}

			});

		}

		@Override
		public void stop() {
			this.executor.shutdownNow();
		}

	}
}
