/*
 * Copyright 2002-2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.springframework.amqp.rabbit.log4j.config.server;

import org.apache.log4j.Level;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.log4j.converter.AmqpLogMessageConverter;
import org.springframework.amqp.rabbit.log4j.web.controller.Log4JSampleControllerA;
import org.springframework.amqp.rabbit.log4j.web.controller.Log4JSampleControllerB;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Server.
 * 
 * @author Tomas Lukosius
 */
@Configuration
public class RabbitServerConfiguration {

	/**
	 * Shared topic exchange used for publishing log4j messages (e.g. debug statements)
	 */
	protected static String LOG_EXCHANGE_NAME = "app.log4j.log";
	protected static String LOG_ERROR_QUEUE_NAME = "Log4JSample.ERROR";

	private int port = 5672;

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
		connectionFactory.setUsername("guest");
		connectionFactory.setPassword("guest");
		connectionFactory.setPort(port);
		return connectionFactory;
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		template.setMessageConverter(amqpLogMessageConverter());
		return template;
	}

	@Bean
	public MessageConverter amqpLogMessageConverter() {
		return new AmqpLogMessageConverter();
	}

	@Bean
	public TopicExchange logExchange() {
		return new TopicExchange(LOG_EXCHANGE_NAME, false, false);
	}

	/**
	 * @return the admin bean that can declare queues etc.
	 */
	@Bean
	public AmqpAdmin amqpAdmin() {
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
		rabbitAdmin.setAutoStartup(true);
		return rabbitAdmin;
	}

	// Queues for component A
	@Bean
	public Queue infoQueueA() {
		return new Queue(Log4JSampleControllerA.class.getSimpleName() + "." + Level.INFO.toString());
	}

	@Bean
	public Queue debugQueueA() {
		return new Queue(Log4JSampleControllerA.class.getSimpleName() + "." + Level.DEBUG.toString());
	}

	@Bean
	public Queue warnQueueA() {
		return new Queue(Log4JSampleControllerA.class.getSimpleName() + "." + Level.WARN.toString());
	}

	@Bean
	public Queue errorQueueA() {
		return new Queue(Log4JSampleControllerA.class.getSimpleName() + "." + Level.ERROR.toString());
	}

	// Queues for component B
	@Bean
	public Queue infoQueueB() {
		return new Queue(Log4JSampleControllerB.class.getSimpleName() + "." + Level.INFO.toString());
	}

	@Bean
	public Queue debugQueueB() {
		return new Queue(Log4JSampleControllerB.class.getSimpleName() + "." + Level.DEBUG.toString());
	}

	@Bean
	public Queue warnQueueB() {
		return new Queue(Log4JSampleControllerB.class.getSimpleName() + "." + Level.WARN.toString());
	}

	@Bean
	public Queue errorQueueB() {
		return new Queue(Log4JSampleControllerB.class.getSimpleName() + "." + Level.ERROR.toString());
	}

	// Common error queue
	@Bean
	public Queue errorQueue() {
		return new Queue(LOG_ERROR_QUEUE_NAME);
	}

	// Bindings for component A
	@Bean
	public Binding infoBindingA() {
		return BindingBuilder.from(infoQueueA()).to(logExchange())
				.with(Log4JSampleControllerA.class.getCanonicalName() + "." + Level.INFO.toString());
	}

	@Bean
	public Binding debugBindingA() {
		return BindingBuilder.from(debugQueueA()).to(logExchange())
				.with(Log4JSampleControllerA.class.getCanonicalName() + "." + Level.DEBUG.toString());
	}

	@Bean
	public Binding warnBindingA() {
		return BindingBuilder.from(warnQueueA()).to(logExchange())
				.with(Log4JSampleControllerA.class.getCanonicalName() + "." + Level.WARN.toString());
	}

	@Bean
	public Binding errorBindingA() {
		return BindingBuilder.from(errorQueueA()).to(logExchange())
				.with(Log4JSampleControllerA.class.getCanonicalName() + "." + Level.ERROR.toString());
	}

	@Bean
	public Binding commonErrorBindingA() {
		return BindingBuilder.from(errorQueue()).to(logExchange())
				.with(getErrorQueueRoutingKey(Log4JSampleControllerA.class));
	}

	// Bindings for component B
	@Bean
	public Binding infoBindingB() {
		return BindingBuilder.from(infoQueueB()).to(logExchange())
				.with(Log4JSampleControllerB.class.getCanonicalName() + "." + Level.INFO.toString());
	}

	@Bean
	public Binding debugBindingB() {
		return BindingBuilder.from(debugQueueB()).to(logExchange())
				.with(Log4JSampleControllerB.class.getCanonicalName() + "." + Level.DEBUG.toString());
	}

	@Bean
	public Binding warnBindingB() {
		return BindingBuilder.from(warnQueueB()).to(logExchange())
				.with(Log4JSampleControllerB.class.getCanonicalName() + "." + Level.WARN.toString());
	}


	@Bean
	public Binding errorBindingB() {
		return BindingBuilder.from(errorQueueB()).to(logExchange())
				.with(Log4JSampleControllerB.class.getCanonicalName() + "." + Level.ERROR.toString());
	}
	
	@Bean
	public Binding commonErrorBindingB() {
		return BindingBuilder.from(errorQueue()).to(logExchange())
				.with(getErrorQueueRoutingKey(Log4JSampleControllerB.class));
	}

	/**
	 * Creates ERROR routing key with '*' instead of class name. If class is 'org.s2.MyClass' - routing key will be
	 * 'org.s2.*.ERROR'. This routing key should be used to bind topic exchange with error queue.
	 * @param clazz
	 * @return
	 */
	private String getErrorQueueRoutingKey(Class<?> clazz) {
		String routingKey = clazz.getCanonicalName().replace(clazz.getSimpleName(), "*");

		return routingKey + "." + Level.ERROR.toString();
	}
}
