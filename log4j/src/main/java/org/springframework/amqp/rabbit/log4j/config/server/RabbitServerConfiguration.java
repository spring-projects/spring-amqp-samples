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
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.log4j.listener.AmqpLogMessageConverter;
import org.springframework.amqp.rabbit.log4j.listener.AmqpLogMessageListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration for Server.
 * 
 * @author Tomas Lukosius
 * @author Dave Syer
 */
@Configuration
@Import(PropertyPlaceholderConfiguration.class)
public class RabbitServerConfiguration {

	/**
	 * Shared topic exchange used for publishing log4j messages (e.g. debug statements)
	 */
	public static String LOG_EXCHANGE_NAME = "app.log4j.log";
	public static String LOG_QUEUE_NAME = "app.log4j.demo";
	public static String LOG_ALL_INFO_ROUTING_KEY = "#." + Level.INFO.toString();

	@Value("${amqp.port:5672}")
	private int port = 5672;

	@Value("${amqp.username:guest}")
	private String username = "guest";

	@Value("${amqp.password:guest}")
	private String password = "guest";

	@Value("${amqp.vhost:/}")
	private String virtualHost = "/";

	@Value("${amqp.host:localhost}")
	private String host = "localhost";

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		connectionFactory.setVirtualHost(virtualHost);
		connectionFactory.setPort(port);
		return connectionFactory;
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

	@Bean
	public Queue queue() {
		return new Queue(LOG_QUEUE_NAME);
	}

	@Bean
	public Binding binding() {
		return BindingBuilder.bind(queue()).to(logExchange()).with(LOG_ALL_INFO_ROUTING_KEY);
	}

	@Bean
	public AmqpLogMessageListener messageListener() {
		return new AmqpLogMessageListener();
	}

	@Bean
	public SimpleMessageListenerContainer listenerContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory());
		container.setQueueNames(LOG_QUEUE_NAME);

		MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(messageListener(),
				new AmqpLogMessageConverter());
		listenerAdapter.setDefaultListenerMethod("handleLog");

		container.setMessageListener(listenerAdapter);
		return container;
	}
}

/**
 * Configuration for property placeholders (enables system and OS placeholders with <code>&#64;Value(${...})</code>).
 * @author Dave Syer
 */
@Configuration
class PropertyPlaceholderConfiguration {
	@Bean
	public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new PropertyPlaceholderConfigurer();
	}
}
