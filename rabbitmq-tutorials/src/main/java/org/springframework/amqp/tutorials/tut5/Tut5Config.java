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
package org.springframework.amqp.tutorials.tut5;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.tutorials.util.SpringAwareExecutorWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Gary Russell
 *
 */
@Profile("tut5")
@Configuration
public class Tut5Config {

	@Bean
	public TopicExchange topic() {
		return new TopicExchange("tut.topic");
	}

	@Profile("receiver")
	public static class ReceiverConfig {
		@Bean
		public Tut5Receiver receiver() {
	 	 	return new Tut5Receiver();
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
		private TopicExchange topic;

		@Bean
		public Binding binding1a() {
			return BindingBuilder.bind(autoDeleteQueue1()).to(topic).with("*.orange.*");
		}

		@Bean
		public Binding binding1b() {
			return BindingBuilder.bind(autoDeleteQueue1()).to(topic).with("*.*.rabbit");
		}

		@Bean
		public Binding binding2a() {
			return BindingBuilder.bind(autoDeleteQueue2()).to(topic).with("lazy.#");
		}
	}

	@Profile("sender")
	@Configuration
	public static class SenderConfig
	{
		@Bean
		public Lifecycle wrappedSender(Tut5Sender sender) {
			return new SpringAwareExecutorWrapper(sender);
		}
		
		@Bean
		public Tut5Sender sender() {
			return new Tut5Sender();
		}
	}

}
