/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.amqp.samples.confirms;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.CorrelationData.Confirm;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringRabbitConfirmsReturnsApplication {

	private static final String QUEUE = "spring.publisher.sample";

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(SpringRabbitConfirmsReturnsApplication.class,
				args);
		context.close();
	}

	@Autowired
	private RabbitTemplate rabbitTemplate;

	private final CountDownLatch listenLatch = new CountDownLatch(1);

	private void runDemo() throws Exception {
		setupCallbacks();
		// send a message to the default exchange to be routed to the queue
		CorrelationData correlationData = new CorrelationData("Correlation for message 1");
		this.rabbitTemplate.convertAndSend("", QUEUE, "foo", correlationData);
		Confirm confirm = correlationData.getFuture().get(10, TimeUnit.SECONDS);
		System.out.println("Confirm received for good delivery, ack = " + confirm.isAck());
		if (this.listenLatch.await(10, TimeUnit.SECONDS)) {
			System.out.println("Message received by listener");
		}
		else {
			System.out.println("Message NOT received by listener");
		}
		// send a message to the default exchange to be routed to a non-existent queue
		correlationData = new CorrelationData("Correlation for message 2");
		this.rabbitTemplate.convertAndSend("", QUEUE + QUEUE, "bar", message -> {
			System.out.println("Message after conversion: " + message);
			return message;
		}, correlationData);
		confirm = correlationData.getFuture().get(10, TimeUnit.SECONDS);
		System.out.println("Confirm received for send to missing queue, ack = " + confirm.isAck());
		System.out.println("Return received:"  + correlationData.getReturned());
		correlationData = new CorrelationData("Correlation for message 3");
		// send to non-existent exhange - expect nack
		this.rabbitTemplate.convertAndSend(UUID.randomUUID().toString(), QUEUE, "baz", correlationData);
		confirm = correlationData.getFuture().get(10, TimeUnit.SECONDS);
		System.out.println("Confirm received for send to missing exchange, ack = " + confirm.isAck());
	}

	private void setupCallbacks() {
		/*
		 * Confirms/returns enabled in application.properties - add the callbacks here.
		 */
		this.rabbitTemplate.setConfirmCallback((correlation, ack, reason) -> {
			if (correlation != null) {
				System.out.println("Received " + (ack ? " ack " : " nack ") + "for correlation: " + correlation);
			}
		});
		this.rabbitTemplate.setReturnsCallback(returned -> {
			System.out.println("Returned: " + returned.getMessage() + "\nreplyCode: " + returned.getReplyCode()
					+ "\nreplyText: " + returned.getReplyText() + "\nexchange/rk: "
					+ returned.getExchange() + "/" + returned.getRoutingKey());
		});
	}


	@Bean
	public ApplicationRunner runner() {
		return args -> runDemo();
	}

	@Bean
	public Queue queue() {
		return new Queue(QUEUE, false, false, true);
	}

	@RabbitListener(queues = QUEUE)
	public void listen(String in) {
		System.out.println("Listener received: " + in);
		this.listenLatch.countDown();
	}

}
