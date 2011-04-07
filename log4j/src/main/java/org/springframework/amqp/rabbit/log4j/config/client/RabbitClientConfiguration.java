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

package org.springframework.amqp.rabbit.log4j.config.client;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.amqp.rabbit.log4j.web.controller.ControllerA;
import org.springframework.amqp.rabbit.log4j.web.controller.ControllerB;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides shared configuration between Client and Server.
 * <p>
 * The abstract method configureRabbitTemplate lets the Client and Server further customize the rabbit template to their
 * specific needs.
 * 
 * @author Tomas Lukosius
 */
@Configuration
public class RabbitClientConfiguration {

	private static Class<?>[] controllerClasses = new Class<?>[] { ControllerA.class, ControllerB.class };

	/**
	 * Shared topic exchange used for publishing log4j messages (e.g. debug statements)
	 */
	protected static String LOG_EXCHANGE_NAME = "app.log4j.log";

	private int port = 5672;

	@Bean
	public ConnectionFactory connectionFactory() {
		// TODO make it possible to customize in subclasses.
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
		connectionFactory.setUsername("guest");
		connectionFactory.setPassword("guest");
		connectionFactory.setPort(port);
		return connectionFactory;
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		template.setMessageConverter(jsonMessageConverter());
		return template;
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new JsonMessageConverter();
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
		return rabbitAdmin;
	}

	// @Bean
	// @Scope(BeanDefinition.SCOPE_PROTOTYPE)
	public Queue logQueue(String queueName) {
		Queue q = new Queue(queueName);
		amqpAdmin().declareQueue(q);
		return q;
	}

	/**
	 * Binds to the market data exchange. Interested in any stock quotes.
	 */
	@Bean
	public List<Binding> queueBindings() {
		List<Binding> list = new ArrayList<Binding>(2);
		String queueName;
		for (Class<?> c : controllerClasses) {
			queueName = c.getCanonicalName() + "." + Level.INFO.toString();
			list.add(BindingBuilder.from(logQueue(queueName)).to(logExchange()).with(queueName));
			queueName = c.getCanonicalName() + "." + Level.DEBUG.toString();
			list.add(BindingBuilder.from(logQueue(queueName)).to(logExchange()).with(queueName));
			queueName = c.getCanonicalName() + "." + Level.WARN.toString();
			list.add(BindingBuilder.from(logQueue(queueName)).to(logExchange()).with(queueName));
			queueName = c.getCanonicalName() + "." + Level.ERROR.toString();
			list.add(BindingBuilder.from(logQueue(queueName)).to(logExchange()).with(queueName));
		}

		// Declare bindings on exchange
		for (Binding binding : list) {
			amqpAdmin().declareBinding(binding);
		}

		return list;
	}
}
