/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.amqp.tutorials;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * A RabbitMQ client (sender) interactions emulator.
 * Accepts {@link #task} and and invokes it with the {@code fixedDelay = 1000}
 * until context stops.
 *
 * @author Artem Bilan
 */
@Service
@Profile({"sender", "client"})
@EnableScheduling
public class ClientEmulator {

	@Autowired
	private Runnable task;

	private volatile boolean stopped;

	@EventListener(ContextClosedEvent.class)
	public void contextClosed() {
		this.stopped = true;
	}

	@Scheduled(fixedDelay = 1000, initialDelay = 500)
	public void runTask() {
		if (!this.stopped) {
			this.task.run();
		}
	}

}
