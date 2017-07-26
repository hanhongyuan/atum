package com.oasis.atum.wechat.infrastructure.config;


import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.serialization.Serializer;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

/**
 * 消息队列配置
 */
@Slf4j
@Configuration
public class AmqpConfiguration
{
	@Value("${axon.amqp.exchange}")
	private String exchangeName;

	@Bean
	public Exchange exchange()
	{
		return ExchangeBuilder.fanoutExchange(exchangeName).durable(true).build();
	}

	@Bean
	public Queue queue()
	{
		return new Queue("wechatQueue", true);
	}

	@Bean
	public Binding queueBinding()
	{
		return BindingBuilder.bind(queue()).to(exchange()).with("").noargs();
	}

	@Bean
	public SpringAMQPMessageSource queueMessageSource(final Serializer serializer)
	{
		return new SpringAMQPMessageSource(serializer)
		{
			@RabbitListener(queues = "wechatQueue")
			@Override
			@Transactional
			public void onMessage(Message message, Channel channel) throws Exception
			{
				log.info("消息接收: {}", message.toString());
				super.onMessage(message, channel);
			}
		};
	}
}
