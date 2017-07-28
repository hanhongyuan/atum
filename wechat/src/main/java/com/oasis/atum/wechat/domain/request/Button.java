package com.oasis.atum.wechat.domain.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.oasis.atum.wechat.domain.enums.MenuType;
import lombok.Builder;

import java.util.List;

/**
 * 菜单按钮
 */
@Builder
public class Button
{
	public final MenuType     type;
	public final String       name;
	public final String       key;
	public final String       url;
	@JSONField(name = "media_id")
	public final String       mediaId;
	@JSONField(name = "sub_button")
	public final List<Button> subButton;

	public Button(final MenuType type, final String name, final String key, final String url, final String mediaId,
								final List<Button> subButton)
	{
		this.type = type;
		this.name = name;
		this.key = key;
		this.url = url;
		this.mediaId = mediaId;
		this.subButton = subButton;
	}

	public Button(final com.oasis.atum.wechat.domain.entity.Menu menu)
	{
		this.type = menu.getType();
		this.name = menu.getName();
		this.key = menu.getMenuKey();
		this.url = menu.getUri();
		this.mediaId = menu.getMediaId();
		this.subButton = null;
	}

	public Button(final com.oasis.atum.wechat.domain.entity.Menu menu, final List<Button> subButton)
	{
		this.type = menu.getType();
		this.name = menu.getName();
		this.key = menu.getMenuKey();
		this.url = menu.getUri();
		this.mediaId = menu.getMediaId();
		this.subButton = subButton;
	}
}