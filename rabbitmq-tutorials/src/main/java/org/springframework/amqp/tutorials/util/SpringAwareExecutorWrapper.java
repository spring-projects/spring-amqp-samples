package org.springframework.amqp.tutorials.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.Lifecycle;

public class SpringAwareExecutorWrapper implements Lifecycle {

	private ExecutorService executor;
	private final Runnable runner;

	public SpringAwareExecutorWrapper(Runnable sender) {
		this.runner = sender;
	}

	@Override
	public boolean isRunning() {
		return this.executor != null && !this.executor.isShutdown();
	}

	@Override
	public void start() {
		this.executor = Executors.newSingleThreadExecutor();
		this.executor.execute(runner);
	}

	@Override
	public void stop() {
		this.executor.shutdownNow();
	}
}
