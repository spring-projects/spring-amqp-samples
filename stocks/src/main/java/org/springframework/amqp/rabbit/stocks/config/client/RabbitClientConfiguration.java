/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.amqp.rabbit.stocks.config.client;


import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.stocks.config.AbstractStockAppRabbitConfiguration;
import org.springframework.amqp.rabbit.stocks.gateway.RabbitStockServiceGateway;
import org.springframework.amqp.rabbit.stocks.gateway.StockServiceGateway;
import org.springframework.amqp.rabbit.stocks.handler.ClientHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures RabbitTemplate and creates the Trader queue and binding for the client.
 *
 * @author Mark Pollack
 * @author Mark Fisher
 * @author Gary Russell
 */
@Configuration
public class RabbitClientConfiguration extends AbstractStockAppRabbitConfiguration {

	@Value("${stocks.quote.pattern}")
	private String marketDataRoutingKey;

	@Autowired
	private ClientHandler clientHandler;

	/**
	 * The client's template will by default send to the exchange defined
	 * in {@link org.springframework.amqp.rabbit.config.AbstractRabbitConfiguration#rabbitTemplate()}
	 * with the routing key {@link AbstractStockAppRabbitConfiguration#STOCK_REQUEST_QUEUE_NAME}
	 * <p>
	 * The default exchange will delivery to a queue whose name matches the routing key value.
	 */
	@Override
	public void configureRabbitTemplate(RabbitTemplate rabbitTemplate) {
		rabbitTemplate.setRoutingKey(STOCK_REQUEST_QUEUE_NAME);
	}

	@Bean
	public StockServiceGateway stockServiceGateway() {
		RabbitStockServiceGateway gateway = new RabbitStockServiceGateway();
		gateway.setRabbitOperations(rabbitTemplate());
		gateway.setDefaultReplyTo(traderJoeQueue().getName());
		return gateway;
	}

	@Bean
	public SimpleMessageListenerContainer messageListenerContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
		container.setQueues(marketDataQueue(), traderJoeQueue());
		container.setMessageListener(messageListenerAdapter());
		container.setAcknowledgeMode(AcknowledgeMode.AUTO);
		return container;

		//container(using(connectionFactory()).listenToQueues(marketDataQueue(), traderJoeQueue()).withListener(messageListenerAdapter()).
	}

	@Bean
	public MessageListenerAdapter messageListenerAdapter() {
		return new MessageListenerAdapter(clientHandler, jsonMessageConverter());
	}


	// Broker Configuration

//	@PostContruct
//	public void declareClientBrokerConfiguration() {
//		declare(marketDataQueue);
//		declare(new Binding(marketDataQueue, MARKET_DATA_EXCHANGE, marketDataRoutingKey));
//		declare(traderJoeQueue);
//		// no need to bind traderJoeQueue as it is automatically bound to the default direct exchanage, which is what we will use
//
//		//add as many declare statements as needed like a script.
//	}

	@Bean
	public Queue marketDataQueue() {
		return new AnonymousQueue();
	}

	/**
	 * Binds to the market data exchange. Interested in any stock quotes.
	 */
	@Bean
	public Binding marketDataBinding() {
		return BindingBuilder.bind(marketDataQueue()).to(marketDataExchange()).with(marketDataRoutingKey);
	}

	/**
	 * This queue does not need a binding, since it relies on the default exchange.
	 */
	@Bean
	public Queue traderJoeQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public AmqpAdmin rabbitAdmin() {
		return new RabbitAdmin(connectionFactory());
	}

}
