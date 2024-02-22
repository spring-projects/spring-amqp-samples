package org.springframework.amqp.samples.streams;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;
import jakarta.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;
import org.springframework.rabbit.stream.producer.RabbitStreamOperations;
import org.springframework.rabbit.stream.support.StreamAdmin;

@SpringBootApplication
public class SpringRabbitStreamsApplication implements BeanFactoryAware {

	private AutowireCapableBeanFactory beanFactory;

	public static void main(String[] args) {
		SpringApplication.run(SpringRabbitStreamsApplication.class, args);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = (AutowireCapableBeanFactory) beanFactory;
	}

	@PostConstruct
	public void addStreamConsumers() {
		for (int i = 0; i < 10; i++) {
			this.beanFactory.initializeBean(new SampleRabbitStreamListener(),
					SampleRabbitStreamListener.class.getSimpleName() + "#" + i);
		}
	}

	@Bean
	ApplicationRunner streamProducer(RabbitStreamOperations rabbitStreamOperations) {
		return args -> {
			CompletableFuture<?>[] sendResults =
					IntStream.range(0, 100)
							.boxed()
							.map((value) -> "Value #" + value)
							.map(rabbitStreamOperations::convertAndSend)
							.toArray(CompletableFuture<?>[]::new);

			CompletableFuture.allOf(sendResults).join();
		};

	}

	@Bean
	StreamAdmin streamAdmin(Environment streamEnvironment, @Value("${spring.rabbitmq.stream.name}") String streamName) {
		return new StreamAdmin(streamEnvironment, sc -> sc.stream(streamName).maxAge(Duration.ofMinutes(1)).create());
	}

	@Bean
	ContainerCustomizer<StreamListenerContainer> streamListenerContainerContainerCustomizer() {
		return (container) ->
				container.setConsumerCustomizer((listenerId, consumerBuilder) ->
						consumerBuilder.offset(OffsetSpecification.last()));
	}

	private static class SampleRabbitStreamListener implements BeanNameAware {

		private static final Log LOG = LogFactory.getLog(SampleRabbitStreamListener.class);

		private String beanName;

		@Override
		public void setBeanName(String name) {
			this.beanName = name;
		}

		@RabbitListener(queues = "${spring.rabbitmq.stream.name}")
		void listen(String data) {
			LOG.info(this.beanName + " received: " + data);
		}

	}

}
