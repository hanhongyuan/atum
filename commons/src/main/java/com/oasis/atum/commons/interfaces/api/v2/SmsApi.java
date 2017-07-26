package com.oasis.atum.commons.interfaces.api.v2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.oasis.atum.base.infrastructure.service.RedisClient;
import com.oasis.atum.base.infrastructure.util.Restful;
import com.oasis.atum.commons.application.service.SmsService;
import com.oasis.atum.commons.interfaces.dto.SmsDTO;
import com.oasis.atum.commons.interfaces.request.SmsCallBack;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 短信接口
 */
@Slf4j
@RestController
@RequestMapping("v2/sms")
public class SmsApi
{
	private final RedisClient redis;
	private final SmsService  service;

	public SmsApi(final RedisClient redis, final SmsService service)
	{
		this.redis = redis;
		this.service = service;
	}

	@PostMapping
	public Mono<ResponseEntity> captcha(@RequestBody Mono<SmsDTO> data)
	{
		log.info("发送验证码");

		return data.map(d ->
		{
			service.sendCaptcha(d);
			return Restful.ok();
		});
	}

	@PostMapping("validation")
	public Mono<ResponseEntity> validation(@RequestBody Mono<SmsDTO> data)
	{
		log.info("校验验证码 =====> ");

		return data.flatMap(d -> redis.get(d.smsType + "=>" + d.mobile)
															 .filter(Objects::nonNull)
															 .map(c -> c.equals(d.captcha))
															 .map(Restful::ok)
															 .defaultIfEmpty(Restful.ok(false)));

	}

	@PostMapping("success")
	@SneakyThrows(Exception.class)
	public ResponseEntity success(final InputStream inputStream)
	{
		getData(inputStream)
			.map(SmsApi::toData)
			.subscribe(service::success);

		return Restful.noContent();
	}

	@PostMapping("fail")
	@SneakyThrows(Exception.class)
	public ResponseEntity fail(final InputStream inputStream)
	{
		getData(inputStream)
			.map(SmsApi::toData)
			.subscribe(service::fail);

		return Restful.noContent();
	}

	@PostMapping("reply")
	public ResponseEntity reply(final InputStream inputStream)
	{
		getData(inputStream)
			.map(SmsApi::toData)
			.subscribe(service::reply);

		return Restful.noContent();
	}

	/**
	 * 从流中解析阿里云短信回调数据转成对象
	 * @param data
	 * @return
	 */
	private static SmsCallBack toData(final String data)
	{
		log.info("阿里云短信回调 =====> {}", data);
		//&分割数据
		return Optional.of(data)
						 //临时处理阿里云意义不明字段 extra
						 .map(s -> s.replace("extra=", "extra=1"))
						 .map(s -> s.split("&"))
						 .map(Stream::of)
						 //=分割键值
						 .map(d -> d.map(s -> s.split("="))
												 //转成JSON
												 .collect(JSONObject::new, (x, y) -> x.put(y[0], y[1]), JSONObject::putAll))
						 .map(j -> JSON.toJavaObject(j, SmsCallBack.class))
						 .get();
	}

	/**
	 * 解析流
	 * @param inputStream
	 * @return
	 */
	private static Mono<String> getData(final InputStream inputStream)
	{
		try
		{
			val bytes = new byte[1024];
			inputStream.read(bytes);
			return Mono.just(new String(bytes, StandardCharsets.UTF_8));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return Mono.empty();
		}
	}
}
