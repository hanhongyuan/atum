package com.oasis.atum.wechat.interfaces.api.v2;

import com.oasis.atum.base.infrastructure.util.CommonUtil;
import com.oasis.atum.base.infrastructure.util.DateUtil;
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
	 * @api {GET} /js-sdk 获取Js-SDK签名
	 * @apiGroup js-sdk
	 * @apiVersion 2.0.0
	 * @apiUse base
	 * @apiParam {String} uri 当前网页的uri,不包含#及其后面部分.
	 * @apiParamExample 请求样例：
	 * ?uri=https://mtest.oasiscare.cn/wxofficial/aboutour/doctorclinic.html?type=yygh
	 * @apiSuccess {String} appId 微信应用ID
	 * @apiSuccess {String} timeStamp 时间戳
	 * @apiSuccess {String} nonceStr 随机字符串
	 * @apiSuccess {String} signature JS-SDK签名
	 * @apiSuccessExample 成功样例:
	 * {
	 * "appId":"wx846dd6977628a1e6",
	 * "timeStamp":"1501833836",
	 * "nonceStr":"25m34j0kngy2p26quajw9k6mbg3598xn",
	 * "signature":"e1728ab56fc49d96e1955180c85255274a38de10"
	 * }
	 */
	@GetMapping
	public Mono<ResponseEntity> getSignature(@RequestParam final String uri)
	{
		log.info("获取Js-SDK签名 =====> {}", uri);

		if (!isOasisNet(uri)) return Mono.just(Restful.ok());
		//当前时间的秒数
		val timeStamp = DateUtil.timeStamp();
		val nonceStr  = CommonUtil.random(32);
		//获取Js-SDK签名
		return client.jsSign(timeStamp, nonceStr, uri)
						 //构建Js-SDK参数
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
