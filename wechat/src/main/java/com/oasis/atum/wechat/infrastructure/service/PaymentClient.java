package com.oasis.atum.wechat.infrastructure.service;

import com.alibaba.fastjson.JSON;
import com.oasis.atum.base.infrastructure.util.BaseUtil;
import com.oasis.atum.base.infrastructure.util.EncryptionUtil;
import com.oasis.atum.wechat.infrastructure.config.WechatConfiguration;
import com.oasis.atum.wechat.infrastructure.util.XMLUtil;
import com.oasis.atum.wechat.interfaces.request.PaymentRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * 微信支付基础服务
 */
@Slf4j
@Component
public class PaymentClient
{
	private final WebClient           client;
	private final WechatConfiguration config;

	public PaymentClient(final WechatConfiguration config)
	{
		this.client = WebClient.builder()
										.baseUrl("https://api.mch.weixin.qq.com/")
										.clientConnector(new ReactorClientHttpConnector())
										.build();
		this.config = config;
	}

	private <T> Mono<String> post(final String uri, final T data)
	{
		return client.post()
						 .uri(uri)
						 .ifNoneMatch("*")
						 .ifModifiedSince(ZonedDateTime.now())
						 .contentType(MediaType.APPLICATION_JSON_UTF8)
						 .body(BodyInserters.fromObject(data))
						 .retrieve()
						 .bodyToMono(String.class);
	}

	/**
	 * 微信支付
	 * @param data
	 * @param type 填了代表小程序支付
	 * @return
	 */
	public Mono<String> payment(final PaymentRequest data, final boolean... type)
	{
		return Mono.justOrEmpty(data)
						 //构建微信支付请求
						 .map(d -> PaymentRequest.builder()
												 .body(d.body)
												 .detail(d.detail)
												 .attach(d.attach)
												 .out_trade_no(d.out_trade_no)
												 //换算
												 .total_fee(new BigDecimal(d.total_fee).multiply(BigDecimal.valueOf(100)).toBigInteger().toString())
												 .spbill_create_ip(d.spbill_create_ip)
												 .trade_type(d.trade_type)
												 .product_id(d.product_id)
												 .openid(d.openid)
												 //设备号
												 .device_info("WEB")
												 .nonce_str(BaseUtil.random(32))
												 //通知回调地址
												 .notify_url(config.getPaymentNotifyUri()))
						 .map(b ->
						 {
							 //小程序
							 if (type.length != 0)
							 {
								 //填了appId就用传的
								 Optional.ofNullable(data.appid).map(b::appid)
									 //没有就取值
									 .orElseGet(() -> b.appid(config.getAppletId()));
								 //商户号
								 b.mch_id(config.getAppletMchId());
							 }
							 //其他支付
							 else
							 {
								 b.appid(config.getAppId());
								 b.mch_id(config.getMchId());
							 }
							 val sign = paymentSign(b.build(), type);
							 val xml  = XMLUtil.toXML(b.sign(sign).build());
							 log.info("发起微信支付 =====> {}", xml);
							 return xml;
						 })
						 .flatMap(s -> post("pay/unifiedorder", s));
	}

	/**
	 * 微信支付签名
	 * @param object
	 * @param type   填了代表小程序
	 * @return
	 */
	public String paymentSign(final Object object, final boolean... type)
	{
		//转JSON字符串并过滤空字段
		return Optional.of(object)
						 .map(JSON::toJSONString)
						 //转JSON对象
						 .map(JSON::parseObject)
						 .flatMap(j -> j.entrySet().stream()
														 //key=value&
														 .map(e -> e.getKey() + "=" + e.getValue() + "&")
														 //排序
														 .sorted()
														 //规约
														 .reduce((x, y) -> x + y)
														 //拼接key
														 .map(s ->
														 {
															 val sb = BaseUtil.getStringBuilder().append(s).append("key=");
															 if (type.length != 0) sb.append(config.getAppletKey());
															 else sb.append(config.getKey());
															 return sb.toString();
														 })
														 //MD5加密
														 .map(EncryptionUtil::MD5)
														 //转大写
														 .map(String::toUpperCase)
						 )
						 //输出
						 .get();
	}
}
