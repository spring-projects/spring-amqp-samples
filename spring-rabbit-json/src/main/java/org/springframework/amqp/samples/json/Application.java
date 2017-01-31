package org.springframework.amqp.samples.json;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;

@SpringBootApplication
public class Application {

	private static final String INFERRED_FOO_QUEUE = "sample.inferred.foo";

	private static final String INFERRED_BAR_QUEUE = "sample.inferred.bar";

	private static final String RECEIVE_AND_CONVERT_QUEUE = "sample.receive.and.convert";

	private static final String MAPPED_QUEUE = "sample.mapped";

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
		ctx.getBean(Application.class).run(args);
		ctx.close();
	}

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private RabbitTemplate jsonRabbitTemplate;

	@Autowired
	private AmqpAdmin rabbitAdmin;

	private volatile CountDownLatch latch = new CountDownLatch(2);

	public void run(String... args) throws Exception {
		String json = "{\"foo\" : \"value\" }";
		Message jsonMessage = MessageBuilder.withBody(json.getBytes())
				.andProperties(MessagePropertiesBuilder.newInstance().setContentType("application/json")
				.build()).build();

		// inferred type

		this.rabbitTemplate.send(INFERRED_FOO_QUEUE, jsonMessage);
		this.rabbitTemplate.send(INFERRED_BAR_QUEUE, jsonMessage);
		this.latch.await(10, TimeUnit.SECONDS);

		// convertAndReceive with type

		this.rabbitTemplate.send(RECEIVE_AND_CONVERT_QUEUE, jsonMessage);
		this.rabbitTemplate.send(RECEIVE_AND_CONVERT_QUEUE, jsonMessage);

		Foo foo = this.jsonRabbitTemplate.receiveAndConvert(RECEIVE_AND_CONVERT_QUEUE, 10_000,
				new ParameterizedTypeReference<Foo>() { });
		System.out.println("Expected a Foo, got a " + foo);
		Bar bar = this.jsonRabbitTemplate.receiveAndConvert(RECEIVE_AND_CONVERT_QUEUE, 10_000,
				new ParameterizedTypeReference<Bar>() { });
		System.out.println("Expected a Bar, got a " + bar);

		// Mapped type information with legacy POJO listener

		this.latch = new CountDownLatch(2);
		jsonMessage.getMessageProperties().setHeader("__TypeId__", "foo");
		this.rabbitTemplate.send(MAPPED_QUEUE, jsonMessage);
		jsonMessage.getMessageProperties().setHeader("__TypeId__", "bar");
		this.rabbitTemplate.send(MAPPED_QUEUE, jsonMessage);
		this.latch.await(10, TimeUnit.SECONDS);

		this.rabbitAdmin.deleteQueue(RECEIVE_AND_CONVERT_QUEUE);
	}

	@RabbitListener(queues = INFERRED_FOO_QUEUE)
	public void listenForAFoo(Foo foo) {
		System.out.println("Expected a Foo, got a " + foo);
		this.latch.countDown();
	}

	@RabbitListener(queues = INFERRED_BAR_QUEUE)
	public void listenForAFoo(Bar bar) {
		System.out.println("Expected a Bar, got a " + bar);
		this.latch.countDown();
	}

	@Bean
	public SimpleMessageListenerContainer legacyPojoListener(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
		container.setQueueNames(MAPPED_QUEUE);
		MessageListenerAdapter messageListener = new MessageListenerAdapter(new Object() {

			@SuppressWarnings("unused")
			public void handleMessage(Object object) {
				System.out.println("Got a " + object);
				Application.this.latch.countDown();
			}

		});
		Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter();
		DefaultClassMapper classMapper = new DefaultClassMapper();
		Map<String, Class<?>> idClassMapping = new HashMap<>();
		idClassMapping.put("foo", Foo.class);
		idClassMapping.put("bar", Bar.class);
		classMapper.setIdClassMapping(idClassMapping);
		jsonConverter.setClassMapper(classMapper);
		messageListener.setMessageConverter(jsonConverter);
		container.setMessageListener(messageListener);
		return container;
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		return new RabbitTemplate(connectionFactory);
	}

	@Bean
	public RabbitTemplate jsonRabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(jsonConverter());
		return template;
	}

	@Bean
	public MessageConverter jsonConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public Queue inferredFoo() {
		return new AnonymousQueue(() -> INFERRED_FOO_QUEUE);
	}

	@Bean
	public Queue inferredBar() {
		return new AnonymousQueue(() -> INFERRED_BAR_QUEUE);
	}

	@Bean
	public Queue convertAndReceive() {
		return new Queue(RECEIVE_AND_CONVERT_QUEUE);
	}

	@Bean
	public Queue mapped() {
		return new AnonymousQueue(() -> MAPPED_QUEUE);
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
			return getClass().getSimpleName() + " [foo=" + this.foo + "]";
		}

	}

	public static class Bar extends Foo {

		public Bar() {
			super();
		}

		public Bar(String foo) {
			super(foo);
		}

	}

}
