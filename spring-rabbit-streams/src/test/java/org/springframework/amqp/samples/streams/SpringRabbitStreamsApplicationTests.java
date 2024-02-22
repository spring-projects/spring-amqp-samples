package org.springframework.amqp.samples.streams;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
@DirtiesContext
class SpringRabbitStreamsApplicationTests {

	@Test
	void verifyAllStreamListenerReceivesAllData(CapturedOutput output) {
		IntStream.range(0, 10)
				.forEach((index) ->
						await().untilAsserted(() ->
								assertThat(output.getOut())
										.contains("SampleRabbitStreamListener#" + index + " received: Value #99")));
	}

}
