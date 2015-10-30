/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.amqp.tutorials.tut4.receiver;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.tutorials.tut4.CommonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.util.StopWatch;

/**
 *
 * @author Gary Russell
 *
 */
@SpringBootApplication
@Import(CommonConfig.class)
public class ReceiverApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext receiver = SpringApplication.run(ReceiverApplication.class);
		Thread.sleep(60000);
		receiver.close();
	}

	@Bean
	public Queue autoDeleteQueue1() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue autoDeleteQueue2() {
		return new AnonymousQueue();
	}

	@Autowired
	private DirectExchange direct;

	@Bean
	public Binding binding1a() {
		return BindingBuilder.bind(autoDeleteQueue1()).to(direct).with("orange");
	}

	@Bean
	public Binding binding1b() {
		return BindingBuilder.bind(autoDeleteQueue1()).to(direct).with("black");
	}

	@Bean
	public Binding binding2a() {
		return BindingBuilder.bind(autoDeleteQueue2()).to(direct).with("green");
	}

	@Bean
	public Binding binding2b() {
		return BindingBuilder.bind(autoDeleteQueue2()).to(direct).with("black");
	}

	@Bean
	public Receiver receiver() {
 	 	return new Receiver();
	}

	public static class Receiver {

		@RabbitListener(queues="#{autoDeleteQueue1.name}")
		public void receive1(String in) throws InterruptedException {
			receive(in, 1);
		}

		@RabbitListener(queues="#{autoDeleteQueue2.name}")
		public void receive2(String in) throws InterruptedException {
			receive(in, 2);
		}

		public void receive(String in, int receiver) throws InterruptedException {
			StopWatch watch = new StopWatch();
			watch.start();
			System.out.println("instance " + receiver + " [x] Received '" + in + "'");
			dowork(in);
			watch.stop();
			System.out.println("instance " + receiver + " [x] Done in " + watch.getTotalTimeSeconds() + "s");
		}

		private void dowork(String in) throws InterruptedException {
			for (char ch : in.toCharArray()) {
				if (ch == '.') {
					Thread.sleep(1000);
				}
			}
		}

	}

}
