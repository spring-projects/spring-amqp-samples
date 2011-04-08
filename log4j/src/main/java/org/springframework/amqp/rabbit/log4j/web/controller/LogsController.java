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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import org.springframework.amqp.rabbit.log4j.web.domain.AmqpLogMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author tomas.lukosius@opencredo.com
 *
 */
@Controller
public class LogsController {
	private Queue<AmqpLogMessage> logs = new PriorityBlockingQueue<AmqpLogMessage>(100, new QuoteComparator());
	private Queue<AmqpLogMessage> errorLogs = new PriorityBlockingQueue<AmqpLogMessage>(100, new QuoteComparator());
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
	
	public void handleErrorLog(AmqpLogMessage message) {
		long timestamp = System.currentTimeMillis() - timeout;
		for (Iterator<AmqpLogMessage> iterator = errorLogs.iterator(); iterator.hasNext();) {
			AmqpLogMessage quote = iterator.next();
			if (quote.getTimestamp() < timestamp) {
				iterator.remove();
			}
		}
		errorLogs.add(message);
	}
	
	protected List<AmqpLogMessage> handle(Long timestamp, Queue<AmqpLogMessage> logsQueue) {
		if (timestamp == null) {
			timestamp = 0L;
		}
		ArrayList<AmqpLogMessage> list = new ArrayList<AmqpLogMessage>();
		for (AmqpLogMessage log : logsQueue) {
			if (log.getTimestamp() > timestamp) {
				list.add(log);
			}
		}
		Collections.reverse(list);
		return list;
	}
	
	@RequestMapping("/logs")
	@ResponseBody
	public List<AmqpLogMessage> logs(@RequestParam(required = false) Long timestamp) {
		return handle(timestamp, logs);
	}
	
	@RequestMapping("/errorLogs")
	@ResponseBody
	public List<AmqpLogMessage> errorLogs(@RequestParam(required = false) Long timestamp) {
		return handle(timestamp, errorLogs);
	}
	
	private static class QuoteComparator implements Comparator<AmqpLogMessage> {
		public int compare(AmqpLogMessage o1, AmqpLogMessage o2) {
			return new Long(o1.getTimestamp() - o2.getTimestamp()).intValue();
		}

	}

}
