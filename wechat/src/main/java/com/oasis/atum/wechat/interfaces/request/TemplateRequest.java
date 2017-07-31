package com.oasis.atum.wechat.interfaces.request;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Map;

/**
 * 模版消息请求集
 */
public interface TemplateRequest
{
	/**
	 * 发送模版消息
	 */
	@Builder
	final class Send
	{
		@JSONField(name = "touser")
		public final String                  toUser;
		@JSONField(name = "template_id")
		public final String                  templateId;
		@JSONField(name = "url")
		public final String                  uri;
		@JSONField(name = "miniprogram")
		public final TemplateMiniProgram     miniProgram;
		public final Map<String, JSONObject> data;

		@JsonCreator
		public Send(@JsonProperty("toUser") final String toUser, @JsonProperty("templateId") final String templateId,
								@JsonProperty("uri") final String uri, @JsonProperty("miniProgram") final TemplateMiniProgram miniProgram,
								@JsonProperty("data") final Map<String, JSONObject> data)
		{
			this.toUser = toUser;
			this.templateId = templateId;
			this.uri = uri;
			this.miniProgram = miniProgram;
			this.data = data;
		}

		/**
		 * 设置模版ID
		 * @param templateId
		 * @return
		 */
		public TemplateRequest.Send setTemplateId(final String templateId)
		{
			return TemplateRequest.Send.builder()
							 .templateId(templateId)
							 .toUser(this.toUser)
							 .uri(this.uri)
							 .miniProgram(this.miniProgram)
							 .data(this.data)
							 .build();
		}
	}

	/**
	 * 小程序
	 */
	@Builder
	final class TemplateMiniProgram
	{
		@JSONField(name = "appid")
		public final String appId;
		@JSONField(name = "pagepath")
		public final String pagePath;

		@JsonCreator
		public TemplateMiniProgram(@JsonProperty("appId") final String appId, @JsonProperty("pagePath") final String pagePath)
		{
			this.appId = appId;
			this.pagePath = pagePath;
		}
	}


}
