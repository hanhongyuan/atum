package com.oasis.atum.wechat.interfaces.api.v2;

import com.alibaba.fastjson.JSONObject;
import com.oasis.atum.base.infrastructure.util.Restful;
import com.oasis.atum.wechat.application.service.PaymentService;
import com.oasis.atum.wechat.interfaces.request.PaymentRequest;
import com.oasis.atum.wechat.interfaces.response.Payment;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 微信支付接口
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("v2/payments")
public class PaymentApi
{
	private final PaymentService service;

	private static final int    ERR_CODE = 0;
	private static final String ERR_MSG  = "参数错误!";

	@PostMapping("applet")
	public Mono<ResponseEntity> applet(@RequestBody final Mono<PaymentRequest> data)
	{
		log.info("小程序支付");

		return data.flatMap(service::applet).map(Restful::ok).defaultIfEmpty(Restful.badRequest(ERR_CODE, ERR_MSG));
	}

	@PostMapping("h5")
	public Mono<ResponseEntity> h5(@RequestBody final Mono<PaymentRequest> data)
	{
		log.info("h5支付");

		return data.flatMap(service::h5).map(Restful::ok).defaultIfEmpty(Restful.badRequest(ERR_CODE, ERR_MSG));
	}

	@PostMapping("native")
	public Mono<ResponseEntity> qrcode(@RequestBody final Mono<PaymentRequest> data)
	{
		log.info("Native支付");

		return data.flatMap(service::qrcode).map(d -> d.code_url).map(Restful::ok).defaultIfEmpty(Restful.badRequest(ERR_CODE, ERR_MSG));
	}

	@PostMapping("app")
	public Mono<ResponseEntity> app(@RequestBody final Mono<PaymentRequest> data)
	{
		log.info("APP支付");

		return data.flatMap(service::app).map(Restful::ok).defaultIfEmpty(Restful.badRequest(ERR_CODE, ERR_MSG));
	}
}
