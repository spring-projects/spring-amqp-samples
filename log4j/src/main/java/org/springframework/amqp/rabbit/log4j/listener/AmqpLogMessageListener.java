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
package org.springframework.amqp.rabbit.log4j.listener;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;


/**
 * @author tomas.lukosius@opencredo.com
 * 
 */
public class AmqpLogMessageListener {
	private Queue<AmqpLogMessage> logs = new PriorityBlockingQueue<AmqpLogMessage>(100, new QuoteComparator());
	private long timeout = 30000; // 30 seconds of data

	public void handleLog(AmqpLogMessage message) {
		long timestamp = System.currentTimeMillis() - timeout;
		for (Iterator<AmqpLogMessage> iterator = logs.iterator(); iterator.hasNext();) {
			AmqpLogMessage quote = iterator.next();
			if (quote.getTimestamp() < timestamp) {
				iterator.remove();
			}
		}
		logs.add(message);
	}

	public Queue<AmqpLogMessage> getLogs() {
		return logs;
	}

	private static class QuoteComparator implements Comparator<AmqpLogMessage> {
		public int compare(AmqpLogMessage o1, AmqpLogMessage o2) {
			return new Long(o1.getTimestamp() - o2.getTimestamp()).intValue();
		}

	}
}
