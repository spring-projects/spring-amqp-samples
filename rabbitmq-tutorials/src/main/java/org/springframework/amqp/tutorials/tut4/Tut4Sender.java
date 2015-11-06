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
package org.springframework.amqp.tutorials.tut4;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Gary Russell, Scott Deeg
 *
 */
public class Tut4Sender implements Runnable {

	@Autowired
	private RabbitTemplate template;

	@Autowired
	private DirectExchange direct;

	private int index;

	private int count;

	private final String[] keys = {"orange", "black", "green"};

	@Override
	public void run() {
		while (true) {
			StringBuilder builder = new StringBuilder("Hello to ");
			if (++this.index == 3) {
				this.index = 0;
			}
			String key = keys[this.index];
			builder.append(key).append(' ');
			builder.append(Integer.toString(++this.count));
			String message = builder.toString();
			template.convertAndSend(direct.getName(), key, message);
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
}
