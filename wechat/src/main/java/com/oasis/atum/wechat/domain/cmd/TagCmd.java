package com.oasis.atum.wechat.domain.cmd;

import lombok.Builder;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.util.Date;
import java.util.List;

/**
 * 标签命令集
 */
public interface TagCmd
{
	/**
	 * 创建
	 */
	@Builder
	final class Create
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
	final class Update
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
	final class AddFans
	{
		@TargetAggregateIdentifier
		public final String       id;
		public final List<String> openIds;
	}
}
