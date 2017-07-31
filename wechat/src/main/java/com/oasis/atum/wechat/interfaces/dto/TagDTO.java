package com.oasis.atum.wechat.interfaces.dto;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;

/**
 * 标签DTO
 */
@Builder
public class TagDTO
{
	public final String  id;
	public final Integer wxId;
	public final String  name;
	public final Long    fans;

	@JSONCreator
	public TagDTO(@JSONField(name = "id") final String id, @JSONField(name = "wxId") final Integer wxId,
								@JSONField(name = "name") final String name, @JSONField(name = "fans") final Long fans)
	{
		this.id = id;
		this.wxId = wxId;
		this.name = name;
		this.fans = fans;
	}
}
