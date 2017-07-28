package com.oasis.atum.wechat.interfaces.request;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;

/**
 * 小程序
 */
@Builder
public class TemplateMiniProgram
{
	@JSONField(name = "appid")
	public final String appId;
	@JSONField(name = "pagepath")
	public final String pagePath;

	@JSONCreator
	public TemplateMiniProgram(@JSONField(name = "appId") final String appId, @JSONField(name = "pagePath") final String pagePath)
	{
		this.appId = appId;
		this.pagePath = pagePath;
	}
}