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

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.log4j.listener.AmqpLogMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

/**
 * This test is for checking does amqp appender in log4j properties and all bindings are declared correctly.
 * @author tomas.lukosius@opencredo.com
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "org.springframework.amqp.rabbit.log4j.config.server" }, loader = AnnotationConfigContextLoader.class)
public class IntegrationTests {
	protected Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private RabbitAdmin admin;

	@Autowired
	private Queue allInfoQueue;

	@Autowired
	private AmqpLogMessageListener messageListener;

	@Before
	public void setUp() {
		admin.purgeQueue(allInfoQueue.getName(), true);
	}

	@After
	public void clear() throws Exception {

		// Wait for broker communication to finish before trying to stop container
		Thread.sleep(300L);
	}

	@Test
	public void logInfo() throws InterruptedException {

		CountDownLatch latch = new CountDownLatch(1);
		new Thread(new MessageChecker(latch, messageListener)).start();

		logger.info("My message: " + new Date());

		boolean waited = latch.await(5000, TimeUnit.MILLISECONDS);
		assertTrue("Time out waiting for message", waited);
	}

	public static class MessageChecker implements Runnable {

		private final CountDownLatch latch;
		private AmqpLogMessageListener messageListener;

		public MessageChecker(CountDownLatch latch, AmqpLogMessageListener messageListener) {
			super();
			this.latch = latch;
			this.messageListener = messageListener;
		}

		public void run() {
			while (latch.getCount() > 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!CollectionUtils.isEmpty(messageListener.getLogs())) {
					latch.countDown();
				}
			}

		}
	}
}
