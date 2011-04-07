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

import org.apache.log4j.Level;
import org.springframework.amqp.rabbit.log4j.web.AbstractLog4JSampleController;
import org.springframework.amqp.rabbit.log4j.web.domain.MessageLoggedResponse;
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
public class Log4JSampleControllerA extends AbstractLog4JSampleController {
	@RequestMapping(value = "/logA", params = "level=info")
	@ResponseBody
	public MessageLoggedResponse logInfoHandlerHandler(@RequestParam("message") String message) {
		if (StringUtils.hasText(message)) {
			super.logInfo(message);
		}
		return new MessageLoggedResponse("A", Level.INFO.toString(), message);
	}

	@RequestMapping(value = "/logA", params = "level=debug")
	@ResponseBody
	public MessageLoggedResponse logDebugHandlerHandler(@RequestParam("message") String message) {
		if (StringUtils.hasText(message)) {
			super.logDebug(message);
		}

		return new MessageLoggedResponse("A", Level.DEBUG.toString(), message);
	}

	@RequestMapping(value = "/logA", params = "level=warn")
	@ResponseBody
	public MessageLoggedResponse logWarnHandler(@RequestParam("message") String message) {
		if (StringUtils.hasText(message)) {
			super.logWarn(message);
		}
		return new MessageLoggedResponse("A", Level.WARN.toString(), message);
	}

	@RequestMapping(value = "/logA", params = "level=error")
	@ResponseBody
	public MessageLoggedResponse logErrorHandler(@RequestParam("message") String message) {
		if (StringUtils.hasText(message)) {
			super.logError(message);
		}
		return new MessageLoggedResponse("A", Level.ERROR.toString(), message);
	}
}
