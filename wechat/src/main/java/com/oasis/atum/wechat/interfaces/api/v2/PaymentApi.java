package com.oasis.atum.wechat.interfaces.api.v2;

import com.alibaba.fastjson.JSONObject;
import com.oasis.atum.base.infrastructure.util.Restful;
import com.oasis.atum.wechat.application.service.PaymentService;
import com.oasis.atum.wechat.interfaces.request.PaymentRequest;
import com.oasis.atum.wechat.interfaces.response.Payment;
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
@RequestMapping("v2/payments")
public class PaymentApi
{
	private final PaymentService service;

	private static final int    ERR_CODE = 0;
	private static final String ERR_MSG  = "参数错误!";

	public PaymentApi(final PaymentService service)
	{
		this.service = service;
	}

	/**
	 * @api {POST} /payments/applet 小程序支付
	 * @apiGroup payments
	 * @apiVersion 2.0.0
	 * @apiUse base
	 * @apiParam {String} appid 微信应用ID
	 * @apiParam {String} body 商品描述
	 * @apiParam {String} attach 附加数据
	 * @apiParam {String(1..32)} out_trade_no 订单号
	 * @apiParam {String} total_fee 订单金额(元)
	 * @apiParam {String} spbill_create_ip IP
	 * @apiParam {String="JSAPI"} trade_type 交易类型
	 * @apiParam {String} openid 用户openid
	 * @apiParamExample 请求样例：
	 * {
	 * "appid": "wx846dd6977628a1e6",
	 * "body": "腾讯充值中心-QQ会员充值",
	 * "openid": "oopn0suRjouaaW6NBAk-6PypQycY",
	 * "out_trade_no": "201807061628043",
	 * "spbill_create_ip": "127.0.0.1",
	 * "total_fee": 1,
	 * "trade_type": "JSAPI"
	 * }
	 * @apiSuccess {String} appId 微信应用ID
	 * @apiSuccess {String} nonceStr 随机字符串
	 * @apiSuccess {String} timeStamp 时间戳
	 * @apiSuccess {String} package 统一支付接口返回的prepay_id参数值
	 * @apiSuccess {String} signType 签名加密类型
	 * @apiSuccess {String} paySign 微信支付签名
	 * @apiSuccessExample 成功样例:
	 * {
	 * "appId":"wx846dd6977628a1e6",
	 * "nonceStr":"kZAmQWOVWARLt6VK",
	 * "timeStamp":"1295374701",
	 * "package":"prepay_id=wx201708050107167c02f116ca0331435452",
	 * "signType": "MD5",
	 * "paySign":"6A7B86F7F67699C7E44939C73A50BBA6"
	 * }
	 */
	@PostMapping("applet")
	public Mono<ResponseEntity> applet(@RequestBody final Mono<PaymentRequest> data)
	{
		log.info("小程序支付");

		return data.flatMap(service::applet).map(Restful::ok).defaultIfEmpty(Restful.badRequest(ERR_CODE, ERR_MSG));
	}

	/**
	 * @api {POST} /payments/h5 h5支付
	 * @apiGroup payments
	 * @apiVersion 2.0.0
	 * @apiUse base
	 * @apiParam {String} body 商品描述
	 * @apiParam {String} attach 附加数据
	 * @apiParam {String(1..32)} out_trade_no 订单号
	 * @apiParam {String} total_fee 订单金额(元)
	 * @apiParam {String} spbill_create_ip IP
	 * @apiParam {String="JSAPI"} trade_type 交易类型
	 * @apiParam {String} openid 用户openid
	 * @apiParamExample 请求样例：
	 * {
	 * "body": "腾讯充值中心-QQ会员充值",
	 * "openid": "oopn0suRjouaaW6NBAk-6PypQycY",
	 * "out_trade_no": "201807061628043",
	 * "spbill_create_ip": "127.0.0.1",
	 * "total_fee": 1,
	 * "trade_type": "JSAPI"
	 * }
	 * @apiSuccess {String} appId 微信应用ID
	 * @apiSuccess {String} nonceStr 随机字符串
	 * @apiSuccess {String} timeStamp 时间戳
	 * @apiSuccess {String} package 统一支付接口返回的prepay_id参数值
	 * @apiSuccess {String} signType 签名加密类型
	 * @apiSuccess {String} paySign 微信支付签名
	 * @apiSuccessExample 成功样例:
	 * {
	 * "appId":"wx846dd6977628a1e6",
	 * "nonceStr":"kZAmQWOVWARLt6VK",
	 * "timeStamp":"1295374701",
	 * "package":"prepay_id=wx201708050107167c02f116ca0331435452",
	 * "signType": "MD5",
	 * "paySign":"6A7B86F7F67699C7E44939C73A50BBA6"
	 * }
	 */
	@PostMapping("h5")
	public Mono<ResponseEntity> h5(@RequestBody final Mono<PaymentRequest> data)
	{
		log.info("h5支付");

		return data.flatMap(service::h5).map(Restful::ok).defaultIfEmpty(Restful.badRequest(ERR_CODE, ERR_MSG));
	}

	/**
	 * @api {POST} /payments/native 二维码支付
	 * @apiGroup payments
	 * @apiVersion 2.0.0
	 * @apiUse base
	 * @apiParam {String} body 商品描述
	 * @apiParam {String} attach 附加数据
	 * @apiParam {String(1..32)} out_trade_no 订单号
	 * @apiParam {String} total_fee 订单金额(元)
	 * @apiParam {String} spbill_create_ip IP
	 * @apiParam {String="NATIVE"} trade_type 交易类型
	 * @apiParam {String} product_id 商品ID
	 * @apiParamExample 请求样例：
	 * {
	 * "body": "腾讯充值中心-QQ会员充值",
	 * "product_id": "1",
	 * "out_trade_no": "201807061628043",
	 * "spbill_create_ip": "127.0.0.1",
	 * "total_fee": 1,
	 * "trade_type": "NATIVE"
	 * }
	 * @apiSuccess {String} code_url 二维码地址
	 * @apiSuccessExample 成功样例:
	 * {
	 * "code_url":"www.wxtest.com"
	 * }
	 */
	@PostMapping("native")
	public Mono<ResponseEntity> qrcode(@RequestBody final Mono<PaymentRequest> data)
	{
		log.info("Native支付");

		return data.flatMap(service::qrcode).map(d -> d.code_url).map(Restful::ok).defaultIfEmpty(Restful.badRequest(ERR_CODE, ERR_MSG));
	}

	/**
	 * @api {POST} /payments/app app支付
	 * @apiGroup payments
	 * @apiVersion 2.0.0
	 * @apiUse base
	 * @apiParam {String} body 商品描述
	 * @apiParam {String} attach 附加数据
	 * @apiParam {String(1..32)} out_trade_no 订单号
	 * @apiParam {String} total_fee 订单金额(元)
	 * @apiParam {String} spbill_create_ip IP
	 * @apiParam {String="APP"} trade_type 交易类型
	 * @apiParam {String} openid 用户openid
	 * @apiParamExample 请求样例：
	 * {
	 * "body": "腾讯充值中心-QQ会员充值",
	 * "out_trade_no": "201807061628043",
	 * "spbill_create_ip": "127.0.0.1",
	 * "total_fee": 1,
	 * "trade_type": "APP"
	 * }
	 * @apiSuccess {String} appid 微信应用ID
	 * @apiSuccess {String} partnerid 微信商户号
	 * @apiSuccess {String} prepayid 预支付ID
	 * @apiSuccess {String} noncestr 随机字符串
	 * @apiSuccess {String} timestamp 时间戳
	 * @apiSuccess {String} package 拓展字段
	 * @apiSuccess {String} sign 微信支付签名
	 * @apiSuccessExample 成功样例:
	 * {
	 * "appid":"wx846dd6977628a1e6",
	 * "partnerid":"1900000109",
	 * "prepayid":"wx201708050107167c02f116ca0331435452",
	 * "noncestr":"kZAmQWOVWARLt6VK",
	 * "timestamp":"1295374701",
	 * "package":"Sign=WXPay",
	 * "sign":"6A7B86F7F67699C7E44939C73A50BBA6"
	 * }
	 */
	@PostMapping("app")
	public Mono<ResponseEntity> app(@RequestBody final Mono<PaymentRequest> data)
	{
		log.info("APP支付");

		return data.flatMap(service::app).map(Restful::ok).defaultIfEmpty(Restful.badRequest(ERR_CODE, ERR_MSG));
	}
}
