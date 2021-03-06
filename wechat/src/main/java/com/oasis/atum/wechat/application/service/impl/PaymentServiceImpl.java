package com.oasis.atum.wechat.application.service.impl;

import com.oasis.atum.base.infrastructure.util.BaseUtil;
import com.oasis.atum.base.infrastructure.util.DateUtil;
import com.oasis.atum.wechat.application.service.PaymentService;
import com.oasis.atum.wechat.infrastructure.config.WechatConfiguration;
import com.oasis.atum.wechat.infrastructure.service.PaymentClient;
import com.oasis.atum.wechat.infrastructure.util.XMLUtil;
import com.oasis.atum.wechat.interfaces.request.PaymentRequest;
import com.oasis.atum.wechat.interfaces.response.Payment;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 微信支付应用服务实现类
 */
@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService
{
	private final PaymentClient       client;
	private final WechatConfiguration config;

	@Override
	@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
	public Mono<Payment.H5> applet(final PaymentRequest data)
	{
		return sendPayments(data, true)
							 .filter(d -> Objects.nonNull(d.prepay_id))
							 .map(d ->
							 {
								 val builder = Payment.H5.builder()
																	 .appId(Objects.isNull(data.appid) ? config.getAppletId() : data.appid)
																	 .timeStamp(DateUtil.timeStamp())
																	 .nonceStr(BaseUtil.random(32))
																	 .signType("MD5")
																	 .pazkage("prepay_id=" + d.prepay_id);
								 val sign = client.paymentSign(builder.build(), true);

								 return builder.paySign(sign).build();
							 });
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
	public Mono<Payment.H5> jsapi(final PaymentRequest data)
	{
		return sendPayments(data)
							 .filter(d -> Objects.nonNull(d.prepay_id))
							 .map(d ->
							 {
								 val builder = Payment.H5.builder()
																	 .appId(config.getAppId())
																	 .timeStamp(DateUtil.timeStamp())
																	 .nonceStr(BaseUtil.random(32))
																	 .signType("MD5")
																	 .pazkage("prepay_id=" + d.prepay_id);
								 val sign = client.paymentSign(builder.build());

								 return builder.paySign(sign).build();
							 });
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
	public Mono<Payment.Response> qrcode(final PaymentRequest data)
	{
		return sendPayments(data);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
	public Mono<Payment.APP> app(final PaymentRequest data)
	{
		return sendPayments(data)
							 .filter(d -> Objects.nonNull(d.prepay_id))
							 .map(d ->
							 {
								 val builder = Payment.APP.builder()
																	 .appId(config.getAppId())
																	 .partnerId(config.getMchId())
																	 .timeStamp(DateUtil.timeStamp())
																	 .nonceStr(BaseUtil.random(32))
																	 .pazkage("Sign=WXPay")
																	 .prepayId(d.prepay_id);
								 val sign = client.paymentSign(builder.build());
								 return builder.sign(sign).build();
							 });
	}

	private Mono<Payment.Response> sendPayments(final PaymentRequest data, boolean... type)
	{
		return client.payment(data, type)
							 .map(s ->
							 {
								 val xml = XMLUtil.parseXML(s, Payment.Response.class);
								 log.info("微信支付返回 =====> {}", xml);
								 return xml;
							 });
	}
}
