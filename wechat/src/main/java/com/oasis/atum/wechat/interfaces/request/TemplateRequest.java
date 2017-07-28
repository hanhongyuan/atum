package com.oasis.atum.wechat.interfaces.request;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;

import java.util.Map;

/**
 * 模版消息请求
 * Created by ryze on 2017/6/16.
 */
@Builder
public class TemplateRequest
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

	@JSONCreator
	public TemplateRequest(@JSONField(name = "toUser") final String toUser, @JSONField(name = "templateId") final String templateId,
												 @JSONField(name = "uri") final String uri, @JSONField(name = "miniProgram") final TemplateMiniProgram miniProgram,
												 @JSONField(name = "data") final Map<String, JSONObject> data)
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
	public TemplateRequest setTemplateId(final String templateId)
	{
		return TemplateRequest.builder()
						 .templateId(templateId)
						 .toUser(this.toUser)
						 .uri(this.uri)
						 .miniProgram(this.miniProgram)
						 .data(this.data)
						 .build();
	}

}
