package com.oasis.atum.wechat.domain.request;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;

import java.util.List;

/**
 * 微信用户标签请求集
 */
public interface TagRequest
{
	/**
	 * 标签添加粉丝请求
	 */
	@Builder
	final class AddFans
	{
		@JSONField(name = "openid_list")
		public final List<String> openIds;
		@JSONField(name = "tagid")
		public final Integer      wxId;
	}

	/**
	 * 标签创建请求
	 */
	final class Create
	{
		@JSONField(serialize = false)
		public final String id;

		public final JSONObject tag = new JSONObject();

		public Create(final String id)
		{
			this.id = id;
		}

		public void setName(final String name)
		{
			tag.put("name", name);
		}

		public String getName()
		{
			return tag.getString("name");
		}
	}

	/**
	 * 标签修改请求
	 */
	final class Update
	{
		public final JSONObject tag = new JSONObject();

		public void setName(final String name)
		{
			tag.put("name", name);
		}

		public void setId(final Integer id)
		{
			tag.put("id", id);
		}

		public String getName()
		{
			return tag.getString("name");
		}

		public Integer getId()
		{
			return tag.getInteger("id");
		}
	}

}
