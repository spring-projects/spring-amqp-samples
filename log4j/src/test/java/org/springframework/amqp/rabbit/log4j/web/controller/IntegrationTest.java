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
package org.springframework.amqp.rabbit.log4j.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.log4j.web.AbstractLog4JSampleController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This test is for checking does amqp appender in log4j properties and all bindings are declared correctly.
 * @author tomas.lukosius@opencredo.com
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "org.springframework.amqp.rabbit.log4j.config" }, loader = AnnotationConfigContextLoader.class)
public class IntegrationTest {

	private int concurrentConsumers = 1;
	private int txSize = 1;
	private boolean transactional = false;
	private AcknowledgeMode acknowledgeMode = AcknowledgeMode.AUTO;

	private SimpleMessageListenerContainer container;

	@Autowired
	private ConnectionFactory connectionFactory;
	
	@Autowired
	private RabbitAdmin admin;

	@Autowired
	@Qualifier("infoQueueA")
	private Queue infoQueue;
	@Autowired
	@Qualifier("debugQueueA")
	private Queue debugQueue;
	@Autowired
	@Qualifier("warnQueueA")
	private Queue warnQueue;
	@Autowired
	@Qualifier("errorQueueA")
	private Queue errorQueue;
	@Autowired
	@Qualifier("errorQueue")
	private Queue commonErrorQueue;

	private Log4JSampleControllerA logProducer = new Log4JSampleControllerA();

	@Before
	public void setUp() {
		admin.purgeQueue(infoQueue.getName(), true);
		admin.purgeQueue(debugQueue.getName(), true);
		admin.purgeQueue(warnQueue.getName(), true);
		admin.purgeQueue(errorQueue.getName(), true);
		admin.purgeQueue(commonErrorQueue.getName(), true);
	}
	
	@After
	public void clear() throws Exception {
		// Wait for broker communication to finish before trying to stop container
		Thread.sleep(300L);
		if (container != null) {
			container.shutdown();
		}
	}

	@Test
	public void logInfo() throws InterruptedException {
		doTest(Level.INFO_INT, infoQueue.getName());
	}

	@Test
	public void logDebug() throws InterruptedException {
		doTest(Level.DEBUG_INT, debugQueue.getName());
	}

	@Test
	public void logWarn() throws InterruptedException {
		doTest(Level.WARN_INT, warnQueue.getName());
	}

	@Test
	public void logError() throws InterruptedException {
		doTest(Level.ERROR_INT, errorQueue.getName(), commonErrorQueue.getName());
	}

	private void doTest(int logLevel, String... queueNames) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(queueNames.length);
		LastMessageListener listener = new LastMessageListener(latch);
		container = createContainer(listener, connectionFactory, queueNames);

		String logMessage = "msg [" + logLevel + "] " + new Date(System.currentTimeMillis());
		switch (logLevel) {
		case Level.INFO_INT:
			logProducer.logInfo(logMessage);
			break;
		case Level.DEBUG_INT:
			logProducer.logDebug(logMessage);
			break;
		case Level.WARN_INT:
			logProducer.logWarn(logMessage);
			break;
		case Level.ERROR_INT:
			logProducer.logError(logMessage);
			break;

		}

		boolean waited = latch.await(500, TimeUnit.MILLISECONDS);
		assertTrue("Time out waiting for message", waited);

		assertNotNull("Log message expected", listener.getValue());

		if (logLevel != Level.ERROR_INT) {
			assertEquals("Incorrect log message from amqp log4j appender", logMessage, listener.getValue().trim());
		} else {
			// Error message contains stack trace
			assertTrue("Error message should start with log message", listener.getValue().startsWith(logMessage));
			assertTrue("Error message should contain stack trace with error message",
					listener.getValue().contains(AbstractLog4JSampleController.errorMessage));
		}
	}

	private SimpleMessageListenerContainer createContainer(Object listener, ConnectionFactory connectionFactory,
			String... queueNames) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
		container.setMessageListener(new MessageListenerAdapter(listener));
		container.setQueueNames(queueNames);
		container.setTxSize(txSize);
		container.setPrefetchCount(txSize);
		container.setConcurrentConsumers(concurrentConsumers);
		container.setChannelTransacted(transactional);
		container.setAcknowledgeMode(acknowledgeMode);
		container.afterPropertiesSet();
		container.start();
		return container;
	}

	public static class LastMessageListener implements MessageListener {

		private final CountDownLatch latch;

		private String value;

		public LastMessageListener(CountDownLatch latch) {
			this.latch = latch;
		}

		public void onMessage(Message message) {
			value = new String(message.getBody());
			latch.countDown();
		}

		public String getValue() {
			return value;
		}
	}
}
