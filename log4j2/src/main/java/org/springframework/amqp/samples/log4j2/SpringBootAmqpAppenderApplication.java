/*
 * Copyright 2016 the original author or authors.
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

package org.springframework.amqp.samples.log4j2;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author Artem Bilan
 * @since 1.6
 */
@SpringBootApplication
@EnableScheduling
public class SpringBootAmqpAppenderApplication {

	private static final Log logger = LogFactory.getLog(SpringBootAmqpAppenderApplication.class);

	public static void main(String[] args) throws IOException {
		ConfigurableApplicationContext ctx = SpringApplication.run(SpringBootAmqpAppenderApplication.class, args);
		System.out.println("Hit 'Enter' to terminate");
		System.in.read();
		ctx.close();
	}

	@Scheduled(fixedDelay = 1000, initialDelay = 2000)
	public void generateLog() {
		logger.info("Log via AmqpAppender: " + new Date());
	}

	@RabbitListener(bindings = @QueueBinding(
			exchange = @Exchange(value = "log4j2Sample", type = ExchangeTypes.FANOUT),
			value = @org.springframework.amqp.rabbit.annotation.Queue))
	public void echoLogs(String logMessage) {
		System.out.println("Logs over Log4J AmqpAppender: " + logMessage);
	}

}
