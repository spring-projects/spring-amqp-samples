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
package org.springframework.amqp.rabbit.log4j.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author tomas.lukosius@opencredo.com
 *
 */
public class AbstractLog4JSampleController {
	protected Log logger = LogFactory.getLog(this.getClass());
	
	public static String errorMessage = "Simulating failure";
	
	public void logInfo(String message) {
		logger.info(message);
	}
	
	public void logDebug(String message) {
		logger.debug(message);
	}
	
	public void logWarn(String message) {
		logger.warn(message);
	}
	
	public void logError(String message) {
		logger.error(message, new RuntimeException(errorMessage));
	}
}
