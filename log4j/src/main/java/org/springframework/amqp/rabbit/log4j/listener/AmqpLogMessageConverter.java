/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.amqp.rabbit.log4j.listener;

import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.log4j2.AmqpAppender;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.util.CollectionUtils;

/**
 * @author tomas.lukosius@opencredo.com
 *
 */
public class AmqpLogMessageConverter extends AbstractMessageConverter {
	private static Log log = LogFactory.getLog(AmqpLogMessageConverter.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.amqp.support.converter.AbstractMessageConverter#createMessage(java.lang.Object,
	 * org.springframework.amqp.core.MessageProperties)
	 */
	@Override
	protected Message createMessage(Object object, MessageProperties messageProperties) {
		throw new UnsupportedOperationException("Conversion from object to amqp logs message not supported");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.springframework.amqp.support.converter.AbstractMessageConverter#fromMessage(org.springframework.amqp.core
	 * .Message)
	 */
	@Override
	public Object fromMessage(Message message) throws MessageConversionException {
		AmqpLogMessage content = null;
		MessageProperties properties = message.getMessageProperties();
		if (properties != null) {
			String applicationId = properties.getAppId();
			Date timestamp = properties.getTimestamp();
			Map<String, Object> headers = properties.getHeaders();

			if (CollectionUtils.isEmpty(headers)) {
				log.warn("Retrieved log message properties should contain headers");
				return null;
			}

			if (!headers.containsKey(AmqpAppender.CATEGORY_NAME)) {
				log.warn(AmqpAppender.CATEGORY_NAME + " - is expected in log message properties headers");
				return null;
			}

			if (!headers.containsKey(AmqpAppender.CATEGORY_LEVEL)) {
				log.warn(AmqpAppender.CATEGORY_LEVEL + " - is expected in log message properties headers");
				return null;
			}

			String logger = (String) headers.get(AmqpAppender.CATEGORY_NAME);
			String level = (String) headers.get(AmqpAppender.CATEGORY_LEVEL);

			content = new AmqpLogMessage(logger, level, new String(message.getBody()), timestamp.getTime(),
					applicationId);
		} else {
			log.warn("Retrieved log message should contain properties");
		}

		return content;
	}

}
