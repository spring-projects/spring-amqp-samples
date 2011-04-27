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
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.log4j.config.server.RabbitServerConfiguration;
import org.springframework.amqp.rabbit.log4j.listener.AmqpLogMessageListener;
import org.springframework.amqp.rabbit.log4j.web.domain.AmqpLogMessage;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author tomas.lukosius@opencredo.com
 * 
 */
@Controller
public class LogsController implements DisposableBean {
	protected Log logger = LogFactory.getLog(this.getClass());

	private static final String CURRENT_LOG_QUEUE = "CURRENT_LOG_QUEUE";
	private static final String CURRENT_ROUTINGKEY = "CURRENT_ROUTINGKEY";


	@Autowired
	private TopicExchange exchange;

	@Autowired
	private Queue logQueue;

	@Autowired
	private RabbitAdmin admin;

	@Autowired
	private Binding binding;
	
	@Autowired
	private AmqpLogMessageListener messageListener;

	@RequestMapping("/logs")
	@ResponseBody
	public List<AmqpLogMessage> logs(@RequestParam(required = false) Long timestamp, HttpSession session) {
		session.setAttribute(CURRENT_LOG_QUEUE, RabbitServerConfiguration.LOG_QUEUE_NAME);
		session.setAttribute(CURRENT_ROUTINGKEY, binding.getRoutingKey());

		if (timestamp == null) {
			timestamp = 0L;
		}
		ArrayList<AmqpLogMessage> list = new ArrayList<AmqpLogMessage>();
		for (AmqpLogMessage log : messageListener.getLogs()) {
			if (log.getTimestamp() > timestamp) {
				list.add(log);
			}
		}
		Collections.reverse(list);
		return list;
	}

	@RequestMapping("/bindQueue")
	@ResponseBody
	public String addQueue(@RequestParam(required = true, value = "routingkey") String routingKey, HttpSession session) {
		if (!StringUtils.hasText(routingKey)) {
			return "Routing key expected";
		}

		try {
			admin.removeBinding(binding);
			binding = BindingBuilder.from(logQueue).to(exchange).with(routingKey);
			admin.declareBinding(binding);
		} catch (RuntimeException e) {
			logger.error("Failed to declare queue or bind it with exchage", e);
			return e.getMessage();
		}

		session.setAttribute(CURRENT_LOG_QUEUE, RabbitServerConfiguration.LOG_QUEUE_NAME);
		session.setAttribute(CURRENT_ROUTINGKEY, binding.getRoutingKey());

		return "Queue '" + logQueue.getName() + "' was binded with routing key '" + routingKey + "'";
	}

	public void destroy() throws Exception {
		admin.removeBinding(binding);
	}
}
