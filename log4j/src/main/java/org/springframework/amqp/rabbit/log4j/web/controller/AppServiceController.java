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

import java.util.Random;

import org.apache.log4j.Level;
import org.springframework.amqp.rabbit.log4j.service.AbstractService;
import org.springframework.amqp.rabbit.log4j.service.ServiceA;
import org.springframework.amqp.rabbit.log4j.service.ServiceB;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author tomas.lukosius@opencredo.com
 * 
 */
@Controller
public class AppServiceController {
	private static final Random random = new Random();

	private ServiceA serviceA = new ServiceA();
	private ServiceB serviceB = new ServiceB();

	@RequestMapping(value = "/logA", params = "level=info")
	@ResponseBody
	public String logInfoA(@RequestParam("message") String message) {
		if (StringUtils.hasText(message)) {
			serviceA.logInfo(message);
		}
		return Level.INFO.toString() + "[" + serviceA.getClass().getSimpleName() + "] - " + message;
	}

	@RequestMapping(value = "/logA", params = "level=debug")
	@ResponseBody
	public String logDebugA(@RequestParam("message") String message) {
		if (StringUtils.hasText(message)) {
			serviceA.logDebug(message);
		}

		return Level.DEBUG.toString() + "[" + serviceA.getClass().getSimpleName() + "] - " + message;
	}

	@RequestMapping(value = "/logA", params = "level=warn")
	@ResponseBody
	public String logWarnA(@RequestParam("message") String message) {
		if (StringUtils.hasText(message)) {
			serviceA.logWarn(message);
		}
		return Level.WARN.toString() + " [" + serviceA.getClass().getSimpleName() + "] - " + message;
	}

	@RequestMapping(value = "/logA", params = "level=error")
	@ResponseBody
	public String logErrorA(@RequestParam("message") String message) {
		if (StringUtils.hasText(message)) {
			serviceA.logError(message);
		}
		return Level.ERROR.toString() + "[" + serviceA.getClass().getSimpleName() + "] - " + message;
	}

	@RequestMapping(value = "/logB", params = "level=info")
	@ResponseBody
	public String logInfoB(@RequestParam("message") String message) {
		if (StringUtils.hasText(message)) {
			serviceB.logInfo(message);
		}
		return Level.INFO.toString() + "[" + serviceB.getClass().getSimpleName() + "] - " + message;
	}

	@RequestMapping(value = "/logB", params = "level=debug")
	@ResponseBody
	public String logDebugB(@RequestParam("message") String message) {
		if (StringUtils.hasText(message)) {
			serviceB.logDebug(message);
		}
		return Level.DEBUG.toString() + "[" + serviceB.getClass().getSimpleName() + "] - " + message;
	}

	@RequestMapping(value = "/logB", params = "level=warn")
	@ResponseBody
	public String logWarnB(@RequestParam("message") String message) {
		if (StringUtils.hasText(message)) {
			serviceB.logWarn(message);
		}
		return Level.WARN.toString() + "[" + serviceB.getClass().getSimpleName() + "] - " + message;
	}

	@RequestMapping(value = "/logB", params = "level=error")
	@ResponseBody
	public String logErrorB(@RequestParam("message") String message) {
		if (StringUtils.hasText(message)) {
			serviceB.logError(message);
		}
		return Level.ERROR.toString() + "[" + serviceB.getClass().getSimpleName() + "] - " + message;
	}

	@RequestMapping(value = "/randomLog")
	@ResponseBody
	public String randomLog(@RequestParam(value = "count", defaultValue = "10") int count) {
		if (count <= 0) {
			count = 10;
		}
		AbstractService service;

		int countServiceA = 0;
		int countServiceB = 0;
		int countInfo = 0;
		int countDebug = 0;
		int countWarn = 0;
		int countError = 0;

		int serviceNo;
		int operationNo;
		String randomMessage;
		for (int i = 0; i < count; i++) {
			serviceNo = random.nextInt(2);
			operationNo = random.nextInt(4);
			if (serviceNo == 0) {
				service = serviceA;
				countServiceA++;
			} else {
				service = serviceB;
				countServiceB++;
			}

			randomMessage = "Random log - " + System.currentTimeMillis();
			switch (operationNo) {
			case 0:
				// INFO
				service.logInfo(randomMessage);
				countInfo++;
				break;
			case 1:
				// DEBUG
				service.logDebug(randomMessage);
				countDebug++;
				break;
			case 2:
				// WARN
				service.logWarn(randomMessage);
				countWarn++;
				break;
			case 3:
				// ERROR
				service.logError(randomMessage);
				countError++;
				break;
			}
		}
		return String.format("%d random logs [%d for %s, %d for %s]: INFO - %d, DEBUG - %d, WARN - %d, ERROR - %d", count,
				countServiceA, serviceA.getClass().getSimpleName(), countServiceB, serviceB.getClass().getSimpleName(),
				countInfo, countDebug, countWarn, countError);
	}
}
