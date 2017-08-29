package com.oasis.atum.base.infrastructure.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

/**
 * 异步Redis基础服务
 */
@Component
@AllArgsConstructor
public class RedisClient
{
	private final ReactiveRedisTemplate<String, String> template;

	public Mono<String> get(final String key)
	{
		return template.opsForValue().get(key);
	}

	public Mono<JSONObject> getJSONObject(final String key)
	{
		return get(key).map(JSON::parseObject);
	}

	public <T> Mono<T> getData(final String key, final Class<T> clazz)
	{
		return get(key).map(s -> JSON.parseObject(s, clazz));
	}

	public Flux<JSONObject> getJSONArray(final String key)
	{
		return get(key).flux().map(JSON::parseObject);
	}

	public Mono<Boolean> put(final String key, final String value)
	{
		return put(key, value, null);
	}

	public Mono<Boolean> put(final String key, final Object value)
	{
		return put(key, value, null);
	}

	public Mono<Boolean> put(final String key, final String value, final Long expire)
	{
		return template.opsForValue().set(key, value)
						 .filter(d -> Objects.nonNull(expire))
						 .flatMap(d -> template.expire(key, Duration.ofMinutes(expire)));
	}

	public Mono<Boolean> put(final String key, final Object value, final Long expire)
	{
		return put(key, JSON.toJSONString(value), expire);
	}

	public Mono<Long> delete(final String key)
	{
		return template.delete(key);
	}

	public Mono<Boolean> exists(final String key)
	{
		return template.createMono(c -> c.keyCommands().exists(ByteBuffer.wrap(key.getBytes(StandardCharsets.UTF_8))));
	}
}
