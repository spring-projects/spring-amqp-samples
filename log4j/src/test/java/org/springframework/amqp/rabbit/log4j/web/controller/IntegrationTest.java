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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.log4j.web.AbstractLog4JSampleController;
import org.springframework.beans.factory.annotation.Autowired;
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

	private String infoQueueNameA = Log4JSampleControllerA.class.getCanonicalName() + "." + Level.INFO.toString();
	private String debugQueueNameA = Log4JSampleControllerA.class.getCanonicalName() + "." + Level.DEBUG.toString();
	private String warnQueueNameA = Log4JSampleControllerA.class.getCanonicalName() + "." + Level.WARN.toString();
	private String errorQueueNameA = Log4JSampleControllerA.class.getCanonicalName() + "." + Level.ERROR.toString();
	private Log4JSampleControllerA underTest = new Log4JSampleControllerA();

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
		doTest(Level.INFO_INT, infoQueueNameA);
	}

	@Test
	public void logDebug() throws InterruptedException {
		doTest(Level.DEBUG_INT, debugQueueNameA);
	}

	@Test
	public void logWarn() throws InterruptedException {
		doTest(Level.WARN_INT, warnQueueNameA);
	}

	@Test
	public void logError() throws InterruptedException {
		doTest(Level.ERROR_INT, errorQueueNameA);
	}

	private void doTest(int logLevel, String queueName) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		LastMessageListener listener = new LastMessageListener(latch);
		container = createContainer(queueName, listener, connectionFactory);

		String logMessage = "msg [" + logLevel + "] " + new Date(System.currentTimeMillis());
		switch (logLevel) {
		case Level.INFO_INT:
			underTest.logInfo(logMessage);
			break;
		case Level.DEBUG_INT:
			underTest.logDebug(logMessage);
			break;
		case Level.WARN_INT:
			underTest.logWarn(logMessage);
			break;
		case Level.ERROR_INT:
			underTest.logError(logMessage);
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

	private SimpleMessageListenerContainer createContainer(String queueName, Object listener,
			ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
		container.setMessageListener(new MessageListenerAdapter(listener));
		container.setQueueNames(queueName);
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
