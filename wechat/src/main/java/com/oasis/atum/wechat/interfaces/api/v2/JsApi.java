package com.oasis.atum.wechat.interfaces.api.v2;

import com.oasis.atum.base.infrastructure.util.BaseUtil;
import com.oasis.atum.base.infrastructure.util.DateUtil;
import com.oasis.atum.base.infrastructure.util.Restful;
import com.oasis.atum.wechat.infrastructure.config.WechatConfiguration;
import com.oasis.atum.wechat.infrastructure.service.WechatClient;
import com.oasis.atum.wechat.interfaces.response.JsSDK;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URL;

import static io.vavr.API.*;

/**
 * Js-SDK接口
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("v2/js-sdk")
public class JsApi
{
	private final WechatClient        client;
	private final WechatConfiguration config;

	/**
	 * 获取JsApi签名
	 * @param uri 当前网页的uri,不包含#及其后面部分.
	 */
	@GetMapping
	public Mono<ResponseEntity> getSignature(@RequestParam final String uri)
	{
		log.info("获取Js-SDK签名 =====> {}", uri);

		return Match(isOasisNet(uri)).of(
				Case($(false), () -> Mono.just(Restful.ok())),
				Case($(), () ->
				{
					//当前时间的秒数
					val timeStamp = DateUtil.timeStamp();
					val nonceStr  = BaseUtil.random(32);
					//获取Js-SDK签名
					return client.jsSign(timeStamp, nonceStr, uri)
										 //构建jJs-SDK参数
										 .map(s -> JsSDK.builder().appId(config.getAppId())
																	 .nonceStr(nonceStr)
																	 .timeStamp(timeStamp)
																	 .signature(s)
																	 .build())
										 .log()
										 .map(Restful::ok);
				})
		);
	}

	/**
	 * 是否泓华Uri
	 * @param net
	 * @return
	 */
	private Boolean isOasisNet(final String net)
	{
		return Try.of(() ->
		{
			val url  = new URL(net);
			val host = url.getHost();
			return host.indexOf("oasisapp") > 1 || host.indexOf("oasiscare") > 1;
		}).getOrElse(false);
	}
}
