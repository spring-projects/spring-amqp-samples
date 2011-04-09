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
package org.springframework.amqp.rabbit.log4j.web.domain;

import java.text.DateFormat;
import java.util.Date;

/**
 * @author tomas.lukosius@opencredo.com
 * 
 */
public class AmqpLogMessage {
	private String logger;
	private String level;
	private String message;
	private long timestamp;
	private String applicationId;

	private DateFormat format = DateFormat.getTimeInstance();

	public AmqpLogMessage(String logger, String level, String message, long timestamp, String applicationId) {
		super();
		this.logger = logger;
		this.level = level;
		this.message = message;
		this.timestamp = timestamp;
		this.applicationId = applicationId;
	}

	public String getLogger() {
		return logger;
	}

	public String getLevel() {
		return level;
	}

	public String getMessage() {
		return message;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public String getTimeString() {
		return format.format(new Date(timestamp));
	}

	@Override
	public String toString() {
		return "Amqp log: " + logger + ":" + level + " " + message;
	}
}
