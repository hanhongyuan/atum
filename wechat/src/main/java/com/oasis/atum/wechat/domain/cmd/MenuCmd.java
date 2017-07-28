package com.oasis.atum.wechat.domain.cmd;

import com.oasis.atum.wechat.domain.enums.MenuType;
import lombok.Builder;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.util.Date;

/**
 * 菜单命令集
 */
public interface MenuCmd
{
	/**
	 * 创建
	 */
	@Builder
	final class Create implements MenuCmd
	{
		@TargetAggregateIdentifier
		public final String   id;
		public final String   name;
		public final MenuType type;
		public final String   menuKey;
		public final String   uri;
		public final String   mediaId;
		public final String   parentId;
		public final String   content;
		public final String   contentType;
		public final Integer  sort;
		public final boolean  isShow;
		public final String   comment;
		public final Date     createTime;
	}

	/**
	 * 修改
	 */
	@Builder
	final class Update implements MenuCmd
	{
		@TargetAggregateIdentifier
		public final String   id;
		public final String   name;
		public final MenuType type;
		public final String   menuKey;
		public final String   uri;
		public final String   mediaId;
		public final String   parentId;
		public final String   content;
		public final String   contentType;
		public final Integer  sort;
		public final Boolean  isShow;
		public final String   comment;
	}

	/**
	 * 删除
	 */
	final class Delete implements MenuCmd
	{
		@TargetAggregateIdentifier
		public final String id;

		public Delete(final String id)
		{
			this.id = id;
		}
	}
}
