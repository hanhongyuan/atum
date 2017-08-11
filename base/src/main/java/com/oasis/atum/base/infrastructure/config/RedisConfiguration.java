package com.oasis.atum.base.infrastructure.config;

import com.oasis.atum.base.infrastructure.service.RedisClient;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**
 * 异步Redis配置
 */
@Configuration
@AllArgsConstructor
public class RedisConfiguration
{
	private final ReactiveRedisConnectionFactory factory;

	@Bean
	public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(final ReactiveRedisConnectionFactory factory)
	{
		val context = RedisSerializationContext.string();
		return new ReactiveRedisTemplate<>(factory, context);
	}

	/**
	 * 异步Redis
	 * @return
	 */
	@Bean
	public RedisClient redisClient()
	{
		return new RedisClient(reactiveRedisTemplate(factory));
	}
}
