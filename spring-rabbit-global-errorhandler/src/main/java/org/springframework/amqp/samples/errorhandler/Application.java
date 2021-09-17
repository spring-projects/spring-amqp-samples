package org.springframework.amqp.samples.errorhandler;

import org.slf4j.Logger;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ErrorHandler;

@SpringBootApplication
public class Application {

	private static final String TEST_QUEUE = "spring.amqp.global.error.handler.demo";

	private final Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		context.getBean(Application.class).runDemo(context.getBean(RabbitTemplate.class));
		context.close();
	}

	private void runDemo(RabbitTemplate template) throws Exception {
		template.convertAndSend(TEST_QUEUE, new Foo("bar"));
		template.convertAndSend(TEST_QUEUE, new Foo("bar"), m -> {
			return new Message("some bad json".getBytes(), m.getMessageProperties());
		});
		Thread.sleep(5000);
	}

	@RabbitListener(queues = TEST_QUEUE)
	public void handle(Foo in) {
		logger.info("Received: " + in);
	}

	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
			SimpleRabbitListenerContainerFactoryConfigurer configurer) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		factory.setErrorHandler(errorHandler());
		return factory;
	}

	@Bean
	public ErrorHandler errorHandler() {
		return new ConditionalRejectingErrorHandler(new MyFatalExceptionStrategy());
	}

	@Bean
	public Queue queue() {
		return new Queue(TEST_QUEUE, false, false, true);
	}

	@Bean
	public MessageConverter jsonConverter() {
		return new Jackson2JsonMessageConverter();
	}

	public static class MyFatalExceptionStrategy extends ConditionalRejectingErrorHandler.DefaultExceptionStrategy {

		private final Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

		@Override
		public boolean isFatal(Throwable t) {
			if (t instanceof ListenerExecutionFailedException) {
				ListenerExecutionFailedException lefe = (ListenerExecutionFailedException) t;
				logger.error("Failed to process inbound message from queue "
						+ lefe.getFailedMessage().getMessageProperties().getConsumerQueue()
						+ "; failed message: " + lefe.getFailedMessage(), t);
			}
			return super.isFatal(t);
		}

	}

	public static class Foo {

		private String foo;

		public Foo() {
			super();
		}

		public Foo(String foo) {
			this.foo = foo;
		}

		public String getFoo() {
			return this.foo;
		}

		public void setFoo(String foo) {
			this.foo = foo;
		}

		@Override
		public String toString() {
			return "Foo [foo=" + this.foo + "]";
		}

	}
}
