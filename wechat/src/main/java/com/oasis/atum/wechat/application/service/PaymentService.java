package com.oasis.atum.wechat.application.service;

import com.oasis.atum.wechat.interfaces.request.PaymentRequest;
import com.oasis.atum.wechat.interfaces.response.Payment;
import reactor.core.publisher.Mono;

/**
 * 微信支付应用服务
 */
public interface PaymentService
{
	/**
	 * 小程序支付
	 * @param data
	 * @return
	 */
	Mono<Payment.H5> applet(PaymentRequest data);

	/**
	 * H5支付
	 * @param data
	 * @return
	 */
	Mono<Payment.H5> h5(PaymentRequest data);

	/**
	 * 二维码支付
	 * @param data
	 * @return
	 */
	Mono<Payment.Response> qrcode(PaymentRequest data);

	/**
	 * APP支付
	 * @param data
	 * @return
	 */
	Mono<Payment.APP> app(PaymentRequest data);
}
