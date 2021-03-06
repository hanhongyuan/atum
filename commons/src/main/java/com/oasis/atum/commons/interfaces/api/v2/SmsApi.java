package com.oasis.atum.commons.interfaces.api.v2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.oasis.atum.base.infrastructure.service.RedisClient;
import com.oasis.atum.base.infrastructure.util.Restful;
import com.oasis.atum.commons.application.service.SmsService;
import com.oasis.atum.commons.interfaces.dto.SmsDTO;
import com.oasis.atum.commons.interfaces.request.SmsCallBack;
import io.vavr.collection.Stream;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 短信接口
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("v2/sms")
public class SmsApi
{
	private final RedisClient redis;
	private final SmsService  service;

	@PostMapping
	public Mono<ResponseEntity> captcha(@RequestBody Mono<SmsDTO> data)
	{
		log.info("发送验证码");

		return data.flatMap(service::sendCaptcha)
							 .map(v -> Restful.ok());
	}

	@PostMapping("validation")
	public Mono<ResponseEntity> validation(@RequestBody Mono<SmsDTO> data)
	{
		log.info("校验验证码 =====> ");

		return data.flatMap(d -> redis.get(d.smsType + "=>" + d.mobile)
																 .map(c -> c.equals(d.captcha))
																 .map(Restful::ok)
																 .defaultIfEmpty(Restful.ok(false)));

	}

	@PostMapping("success")
	@SneakyThrows(Exception.class)
	public Mono<ResponseEntity> success(final ServerHttpRequest request)
	{
		return getData(request)
							 .map(SmsApi::toData)
							 .map(d -> d.onSuccess(service::success))
							 .map(v -> Restful.noContent());
	}

	@PostMapping("fail")
	@SneakyThrows(Exception.class)
	public Mono<ResponseEntity> fail(final ServerHttpRequest request)
	{
		return getData(request)
							 .map(SmsApi::toData)
							 .map(d -> d.onSuccess(service::fail))
							 .map(v -> Restful.noContent());
	}

	@PostMapping("reply")
	public Mono<ResponseEntity> reply(final ServerHttpRequest request)
	{
		return getData(request)
							 .map(SmsApi::toData)
							 .map(d -> d.onSuccess(service::reply))
							 .map(v -> Restful.noContent());
	}

	/**
	 * 从流中解析阿里云短信回调数据转成对象
	 * @param data
	 * @return
	 */
	private static Try<SmsCallBack> toData(final Try<String> data)
	{
		//&分割数据
		return data.map(s ->
		{
			log.info("阿里云短信回调 =====> {}", data);
			//临时处理阿里云意义不明字段 extra
			return s.replace("extra=", "extra=1");
		}).map(s -> s.split("&"))
							 .map(Stream::of)
							 //=分割键值
							 .map(d -> d.map(s -> s.split("="))
														 //转成JSON
														 .collect(JSONObject::new, (x, y) -> x.put(y[0], y[1]), JSONObject::putAll))
							 .map(j -> JSON.toJavaObject(j, SmsCallBack.class));
	}

	/**
	 * 解析流
	 * @param request
	 * @return
	 */
	private static Mono<Try<String>> getData(final ServerHttpRequest request)
	{
		return request.getBody()
							 .map(DataBuffer::asInputStream)
							 .map(is -> Try.of(() ->
							 {
								 val bytes = new byte[1024];
								 is.read(bytes);
								 return new String(bytes, StandardCharsets.UTF_8);
							 }))
							 .elementAt(0);
	}
}
