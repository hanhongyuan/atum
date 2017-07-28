package com.oasis.atum.wechat.domain.request;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.oasis.atum.wechat.domain.enums.QRCodeType;
import lombok.Builder;
import lombok.val;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 二维码请求
 */
@Builder
public class QRCodeRequest
{
	@JSONField(serialize = false)
	public final String     id;
	@JSONField(name = "action_name")
	public final QRCodeType actionName;
	@JSONField(name = "action_info")
	public final QRCodeInfo qrCodeInfo;
	@JSONField(name = "expire_seconds")
	public final Integer    expireSeconds;

	/**
	 * 长链接转短链接
	 * @param ticket
	 * @return
	 */
	public JSONObject long2Short(final String ticket)
	{
		try
		{
			val json = new JSONObject();
			json.put("action", "long2short");
			json.put("long_url", "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + URLEncoder.encode(ticket, "UTF-8"));
			return json;
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
