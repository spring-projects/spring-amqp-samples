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

/**
 * @author Gary Russell
 *
 */
public class Main {

	public static void main(String[] args) throws Exception {
		String runner = System.getProperty("runner");
		if (runner == null) {
			System.err.println("Needs -Drunner");
			System.exit(1);
		}
		if (runner.equals("tut1.Sender")) {
			org.springframework.amqp.tutorials.tut1.sender.SenderApplication.main(args);
		}
		else if (runner.equals("tut1.Receiver")) {
			org.springframework.amqp.tutorials.tut1.receiver.ReceiverApplication.main(args);
		}
		else if (runner.equals("tut2.Sender")) {
			org.springframework.amqp.tutorials.tut2.sender.SenderApplication.main(args);
		}
		else if (runner.equals("tut2.Receiver")) {
			org.springframework.amqp.tutorials.tut2.receiver.ReceiverApplication.main(args);
		}
		else if (runner.equals("tut3.Sender")) {
			org.springframework.amqp.tutorials.tut3.sender.SenderApplication.main(args);
		}
		else if (runner.equals("tut3.Receiver")) {
			org.springframework.amqp.tutorials.tut3.receiver.ReceiverApplication.main(args);
		}
		else if (runner.equals("tut4.Sender")) {
			org.springframework.amqp.tutorials.tut4.sender.SenderApplication.main(args);
		}
		else if (runner.equals("tut4.Receiver")) {
			org.springframework.amqp.tutorials.tut4.receiver.ReceiverApplication.main(args);
		}
		else if (runner.equals("tut5.Sender")) {
			org.springframework.amqp.tutorials.tut5.sender.SenderApplication.main(args);
		}
		else if (runner.equals("tut5.Receiver")) {
			org.springframework.amqp.tutorials.tut5.receiver.ReceiverApplication.main(args);
		}
		else if (runner.equals("tut6.Client")) {
			org.springframework.amqp.tutorials.tut6.client.ClientApplication.main(args);
		}
		else if (runner.equals("tut6.Server")) {
			org.springframework.amqp.tutorials.tut6.server.ServerApplication.main(args);
		}
		else {
			System.err.println("Unexpected runner: " + runner);
			System.exit(2);
		}
		System.exit(0);
	}

}
