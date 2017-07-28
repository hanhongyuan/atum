package com.oasis.atum.wechat.interfaces.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

/**
 * 微信支付请求
 * Created by ryze on 2017/5/16.
 */
@Builder
@ToString
public class PaymentRequest
{
	public final String appid;
	public final String mch_id;
	public final String device_info;
	public final String nonce_str;
	public final String sign;
	public final String body;
	public final String detail;
	public final String attach;
	public final String out_trade_no;
	public final String total_fee;
	public final String spbill_create_ip;
	public final String notify_url;
	public final String trade_type;
	public final String product_id;
	public final String openid;

	@JsonCreator
	public PaymentRequest(@JsonProperty("appid") final String appid, @JsonProperty("mch_id") final String mch_id,
												@JsonProperty("device_info") final String device_info, @JsonProperty("nonce_str") final String nonce_str,
												@JsonProperty("sign") final String sign, @JsonProperty("body") final String body,
												@JsonProperty("detail") final String detail, @JsonProperty("attach") final String attach,
												@JsonProperty("out_trade_no") final String out_trade_no, @JsonProperty("total_fee") final String total_fee,
												@JsonProperty("spbill_create_ip") final String spbill_create_ip, @JsonProperty("notify_url") final String notify_url,
												@JsonProperty("trade_type") final String trade_type, @JsonProperty("product_id") final String product_id,
												@JsonProperty("openid") final String openid)
	{
		this.appid = appid;
		this.mch_id = mch_id;
		this.device_info = device_info;
		this.nonce_str = nonce_str;
		this.sign = sign;
		this.body = body;
		this.detail = detail;
		this.attach = attach;
		this.out_trade_no = out_trade_no;
		this.total_fee = total_fee;
		this.spbill_create_ip = spbill_create_ip;
		this.notify_url = notify_url;
		this.trade_type = trade_type;
		this.product_id = product_id;
		this.openid = openid;
	}
}
