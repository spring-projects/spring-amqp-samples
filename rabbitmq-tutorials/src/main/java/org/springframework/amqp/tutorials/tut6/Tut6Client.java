package org.springframework.amqp.tutorials.tut6;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

public class Tut6Client implements Runnable {

	@Autowired
	private RabbitTemplate template;

	@Autowired
	private DirectExchange exchange;

	@Override
	public void run() {
		int start = 0;
		while (true) {
			System.out.println(" [x] Requesting fib(" + start++ + ")");
			Integer response = (Integer) template.convertSendAndReceive(exchange.getName(), "rpc", start);
			System.out.println(" [.] Got '" + response + "'");
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}
}
