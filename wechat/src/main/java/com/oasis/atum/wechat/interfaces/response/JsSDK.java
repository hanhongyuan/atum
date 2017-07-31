package com.oasis.atum.wechat.interfaces.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.ToString;

/**
 * Js-SDK响应数据结构
 * Created by ryze on 2017/5/19.
 */
@Builder
@ToString
public class JsSDK
{
	public final String appId;
	@JSONField(name = "timestamp")
	public final long   timeStamp;
	public final String nonceStr;
	public final String signature;

	public JsSDK(final String appId, final long timeStamp, final String nonceStr, final String signature)
	{
		this.appId = appId;
		this.timeStamp = timeStamp;
		this.nonceStr = nonceStr;
		this.signature = signature;
	}
}
