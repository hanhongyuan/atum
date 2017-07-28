package com.oasis.atum.wechat.interfaces.api.v2;

import com.alibaba.fastjson.JSONObject;
import com.oasis.atum.base.infrastructure.service.RedisClient;
import com.oasis.atum.base.infrastructure.util.FileUtil;
import com.oasis.atum.base.infrastructure.util.Restful;
import com.oasis.atum.wechat.infrastructure.service.WechatClient;
import com.oasis.atum.wechat.interfaces.response.OpenId;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 微信公众号接口
 */
@Slf4j
@RestController
@RequestMapping("v2")
public class WechatApi
{
	private final RedisClient  redis;
	private final WechatClient client;

	public WechatApi(final RedisClient redis, final WechatClient client)
	{
		this.redis = redis;
		this.client = client;
	}

	@GetMapping("{openId}")
	public Mono<ResponseEntity> openId(@PathVariable final String openId)
	{
		log.info("用户微信相关信息 =====> {}", openId);
		return redis.getData(openId, OpenId.class).map(Restful::ok);
	}

	public static void main(String[] args)
	{
		val data = FileUtil.read("/usr/local/Cellar/cost.txt");
		val list = data.split("\n");

		//过滤掉小于200的接口
		Flux.fromArray(list).filter(s ->
		{
			val end   = s.indexOf("Invoke_Cost");
			val value = Integer.parseInt(s.substring(end + 12));
			return value >= 200 && !s.contains(".css") && !s.contains(".js");
			//去重
		}).distinct(s -> s.substring(s.indexOf("/"), s.indexOf("Invoke_Cost")))
			//转换成JSON格式
			.map(s ->
			{
				val split = s.substring(s.indexOf("/")).split("Invoke_Cost");
				val json  = new JSONObject();
				json.put("name", split[0].trim());
				json.put("cost", split[1].trim());
				return json;
			})
			//按耗时排序
			.sort((x, y) ->
			{
				val xc = x.getIntValue("cost");
				val yc = y.getIntValue("cost");
				return xc > yc ? -1 : 1;
			})
			.subscribe(System.out::println);
	}

	@GetMapping
	public Mono<Void> get(@RequestParam final String signature, @RequestParam final String timestamp,
												@RequestParam final String nonce, @RequestParam final String echostr,
												final ServerHttpResponse response)
	{
		log.info("校验消息来源是否微信");
		// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
		return client.check(signature, timestamp, nonce).filter(b -> b).flatMap(b ->
		{
			val buffer = response.bufferFactory().allocateBuffer().write(echostr.getBytes(StandardCharsets.UTF_8));
			return response.writeWith(Mono.just(buffer)).then();
		});
	}
//	@GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
}
