package com.oasis.atum.wechat.domain.event;

import lombok.Builder;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.util.Date;
import java.util.List;

/**
 * 标签事件集
 */
public interface TagEvent
{
	/**
	 * 创建
	 */
	@Builder
	final class Created implements TagEvent
	{
		@TargetAggregateIdentifier
		public final String id;
		public final String name;
		public final Date   createTime;
	}

	/**
	 * 修改
	 */
	@Builder
	final class Updated implements TagEvent
	{
		@TargetAggregateIdentifier
		public final String  id;
		public final Integer wxId;
		public final String  name;
	}

	/**
	 * 添加粉丝
	 */
	@Builder
	final class AddedFans implements TagEvent
	{
		@TargetAggregateIdentifier
		public final String       id;
		public final List<String> openIds;
	}
}
