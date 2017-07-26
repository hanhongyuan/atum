package com.oasis.atum.wechat.interfaces.api.v2;

import com.oasis.atum.base.infrastructure.util.CommonUtil;
import com.oasis.atum.base.infrastructure.util.Restful;
import com.oasis.atum.wechat.infrastructure.config.WechatConfiguration;
import com.oasis.atum.wechat.infrastructure.service.WechatClient;
import com.oasis.atum.wechat.interfaces.response.JsSDK;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Js-SDK接口
 */
@Slf4j
@RestController
@RequestMapping("v2/js-sdk")
public class JsApi
{
	private final WechatClient        client;
	private final WechatConfiguration config;

	public JsApi(final WechatClient client, final WechatConfiguration config)
	{
		this.client = client;
		this.config = config;
	}

	/**
	 * 获取JsApi签名
	 * @param uri 当前网页的uri,不包含#及其后面部分.
	 */
	@GetMapping
	public Mono<ResponseEntity> getSignature(@RequestParam final String uri)
	{
		log.info("获取Js-SDK签名 =====> {}", uri);

		if (!isOasisNet(uri)) return Mono.just(Restful.ok());
		//当前时间的秒数
		val timeStamp = System.currentTimeMillis() / 1000;
		val nonceStr  = CommonUtil.random(32);
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
	}

	/**
	 * 是否泓华Uri
	 * @param net
	 * @return
	 */
	private boolean isOasisNet(final String net)
	{
		try
		{
			val url  = new URL(net);
			val host = url.getHost();
			return host.indexOf("oasisapp") > 1 || host.indexOf("oasiscare") > 1;
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
