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
package org.springframework.amqp.tutorials.tut2.receiver;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.tutorials.tut2.CommonConfig;
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
	public Receiver receiver1() {
		return new Receiver(1);
	}

	@Bean
	public Receiver receiver2() {
		return new Receiver(2);
	}

	@RabbitListener(queues="tut.hello")
	public static class Receiver {

		private final int instance;

		public Receiver(int i) {
			this.instance = i;
		}

		@RabbitHandler
		public void receive(String in) throws InterruptedException {
			StopWatch watch = new StopWatch();
			watch.start();
			System.out.println("instance " + this.instance + " [x] Received '" + in + "'");
			dowork(in);
			watch.stop();
			System.out.println("instance " + this.instance + " [x] Done in " + watch.getTotalTimeSeconds() + "s");
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
