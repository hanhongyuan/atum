package com.oasis.atum.wechat.domain.request;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.oasis.atum.wechat.infrastructure.enums.QRCodeType;
import io.vavr.control.Try;
import lombok.Builder;
import lombok.val;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.Supplier;

/**
 * 二维码请求集
 */
public interface QRCodeRequest
{
	/**
	 * 二维码信息值对象
	 */
	final class QRCodeInfo
	{
		public final JSONObject scene = new JSONObject();

		public void setSceneStr(final String sceneStr)
		{
			scene.put("scene_str", sceneStr);
		}

		public void setSceneId(final String sceneId)
		{
			scene.put("scene_id", sceneId);
		}
	}

	/**
	 * 创建
	 */
	@Builder
	final class Create
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
			return Try.of(() ->
			{
				val json = new JSONObject();
				json.put("action", "long2short");
				json.put("long_url", "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + URLEncoder.encode(ticket, "UTF-8"));
				return json;
			}).getOrElseThrow((Supplier<RuntimeException>) RuntimeException::new);
		}
	}
}
