package com.oasis.atum.wechat.interfaces.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.ToString;

/**
 * 微信支付接口响应
 */
public interface Payment
{
	@Builder
	final class APP
	{
		@JSONField(name = "appid")
		public final String appId;
		@JSONField(name = "partnerid")
		public final String partnerId;
		@JSONField(name = "prepayid")
		public final String prepayId;
		@JSONField(name = "noncestr")
		public final String nonceStr;
		@JSONField(name = "timestamp")
		public final long   timeStamp;
		@JSONField(name = "package")
		public final String pazkage;
		public final String sign;
	}

	@Builder
	final class H5
	{
		public final String appId;
		public final String nonceStr;
		public final long   timeStamp;
		@JSONField(name = "package")
		public final String pazkage;
		public final String signType;
		public final String paySign;
	}

	@Builder
	@ToString
	final class Response
	{
		public final String return_code;
		public final String return_msg;

		public final String appid;
		public final String mch_id;
		public final String nonce_str;
		public final String sign;
		public final String result_code;
		public final String err_code;
		public final String err_code_des;
		public final String device_info;

		// return_code result_code 都为SUCCESS才有
		public final String code_url;
		public final String trade_type;
		public final String prepay_id;
	}


}
