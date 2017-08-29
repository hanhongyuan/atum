package com.oasis.atum.wechat.domain.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.oasis.atum.wechat.infrastructure.enums.MenuType;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

/**
 * 菜单请求集
 */
public interface MenuRequest
{
	/**
	 * 菜单按钮
	 */
	@Builder
	@AllArgsConstructor
	final class Button
	{
		public final MenuType     type;
		public final String       name;
		public final String       key;
		public final String       url;
		@JSONField(name = "media_id")
		public final String       mediaId;
		@JSONField(name = "sub_button")
		public final List<Button> subButton;

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

	/**
	 * 个性化菜单匹配规则
	 */
	@Builder
	final class Matchrule
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

	/**
	 * 创建
	 */
	@Builder
	final class Create
	{
		public final List<Button> button;
		public final Matchrule    matchrule;
	}

}
