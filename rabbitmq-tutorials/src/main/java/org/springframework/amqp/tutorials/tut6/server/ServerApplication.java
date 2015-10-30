package org.springframework.amqp.tutorials.tut6.server;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext server = SpringApplication.run(ServerApplication.class, args);
		Thread.sleep(60000);
		server.close();
	}

	@Bean
	public Queue queue() {
		return new Queue("tut.rpc.requests");
	}

	@Bean
	public DirectExchange exchange() {
		return new DirectExchange("tut.rpc");
	}

	@Bean
	public Binding binding() {
		return BindingBuilder.bind(queue()).to(exchange()).with("rpc");
	}

	@Bean
	public Listener listener() {
		return new Listener();
	}

	public static class Listener {

		@RabbitListener(queues="tut.rpc.requests")
		// @SendTo("tut.rpc.replies") used when the client doesn't set replyTo.
		public int fibonacci(int n) {
			System.out.println(" [x] Received request for " + n);
			int result = fib(n);
			System.out.println(" [.] Returned " + result);
			return result;
		}

		public int fib(int n) {
			return n == 0 ? 0 : n == 1 ? 1 : (fib(n - 1) + fib(n - 2));
		}

 	}

}
