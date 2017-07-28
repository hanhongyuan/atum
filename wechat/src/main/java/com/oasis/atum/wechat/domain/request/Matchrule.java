package com.oasis.atum.wechat.domain.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;

/**
 * 个性化菜单匹配规则
 */
@Builder
public class Matchrule
{
	@JSONField(name = "tag_id")
	public final Integer tagId;
	public final String  sex;
	public final String  country;
	public final String  province;
	public final String  city;
	@JSONField(name = "client_platform_type")
	public final String  clientPlatformType;
	public final String  language;

}
