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

import org.springframework.amqp.rabbit.log4j.web.AbstractController;

/**
 * @author tomas.lukosius@opencredo.com
 * 
 */
public class ControllerB extends AbstractController {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.amqp.rabbit.log4j.web.AbstractController#logInfo(java.lang.String)
	 */
	@Override
	protected void logInfo(String message) {
		super.logInfo(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.amqp.rabbit.log4j.web.AbstractController#logDebug(java.lang.String)
	 */
	@Override
	protected void logDebug(String message) {
		super.logDebug(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.amqp.rabbit.log4j.web.AbstractController#logError(java.lang.String, java.lang.String)
	 */
	@Override
	protected void logError(String message, String errorMessage) {
		super.logError(message, errorMessage);
	}
}
