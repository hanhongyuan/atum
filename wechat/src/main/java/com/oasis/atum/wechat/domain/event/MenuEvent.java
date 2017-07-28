package com.oasis.atum.wechat.domain.event;


import com.oasis.atum.wechat.domain.cmd.MenuCmd;
import lombok.Builder;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

/**
 * 菜单事件集
 */
public interface MenuEvent
{
	/**
	 * 创建
	 */
	@Builder
	final class Created implements MenuEvent
	{
		@TargetAggregateIdentifier
		public final String         id;
		public final MenuCmd.Create cmd;
	}

	/**
	 * 修改
	 */
	@Builder
	final class Updated implements MenuEvent
	{
		@TargetAggregateIdentifier
		public final String         id;
		public final MenuCmd.Update cmd;
	}

	/**
	 * 删除
	 */
	final class Deleted implements MenuEvent
	{
		@TargetAggregateIdentifier
		public final String id;

		public Deleted(final String id)
		{
			this.id = id;
		}
	}
}
