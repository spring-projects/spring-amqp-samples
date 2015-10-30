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
package org.springframework.amqp.tutorials.tut5.sender;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.tutorials.tut5.CommonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author Gary Russell
 *
 */
@Import(CommonConfig.class)
@SpringBootApplication
public class SenderApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext sender = SpringApplication.run(SenderApplication.class, args);
		sender.start();
		Thread.sleep(10000);
		sender.close();
	}

	@Bean
	public Sender sender() {
		return new Sender();
	}

	public static class Sender implements Lifecycle {

		private ExecutorService executor;

		@Autowired
		private RabbitTemplate template;

		@Autowired
		private TopicExchange topic;

		@Override
		public boolean isRunning() {
			return this.executor != null && !this.executor.isShutdown();
		}

		@Override
		public void start() {
			this.executor = Executors.newSingleThreadExecutor();
			this.executor.execute(new Runnable() {

				private int index;

				private int count;

				private final String[] keys = {"quick.orange.rabbit", "lazy.orange.elephant", "quick.orange.fox",
						"lazy.brown.fox", "lazy.pink.rabbit", "quick.brown.fox"};

				@Override
				public void run() {
					while (true) {
						StringBuilder builder = new StringBuilder("Hello to ");
						if (++this.index == keys.length) {
							this.index = 0;
						}
						String key = keys[this.index];
						builder.append(key).append(' ');
						builder.append(Integer.toString(++this.count));
						String message = builder.toString();
						template.convertAndSend(topic.getName(), key, message);
						System.out.println(" [x] Sent '" + message + "'");
						try {
							Thread.sleep(1000);
						}
						catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							break;
						}
					}
				}

			});

		}

		@Override
		public void stop() {
			this.executor.shutdownNow();
		}

	}

}
