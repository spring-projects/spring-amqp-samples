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
package org.springframework.amqp.tutorials.tut1.receiver;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.tutorials.tut1.CommonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

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
		Thread.sleep(10000);
		receiver.close();
	}

	@Bean
	public Receiver receiver() {
		return new Receiver();
	}

	@RabbitListener(queues="tut.hello")
	public static class Receiver {

		@RabbitHandler
		public void receive(String in) {
			System.out.println(" [x] Received '" + in + "'");
		}

	}

}
