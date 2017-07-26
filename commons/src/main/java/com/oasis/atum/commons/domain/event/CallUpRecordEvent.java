package com.oasis.atum.commons.domain.event;

import com.oasis.atum.commons.domain.cmd.CallUpRecordCmd;
import lombok.Builder;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

/**
 * 通话记录事件集
 */
public interface CallUpRecordEvent
{
	/**
	 * 创建
	 */
	@Builder
	final class Created implements CallUpRecordEvent
	{
		@TargetAggregateIdentifier
		public final String                 id;
		public final CallUpRecordCmd.Create cmd;
	}

	/**
	 * 修改
	 */
	@Builder
	final class Updated implements CallUpRecordEvent
	{
		@TargetAggregateIdentifier
		public final String                 id;
		public final CallUpRecordCmd.Update cmd;
	}

	/**
	 * 保存
	 */
	@Builder
	final class Saved implements CallUpRecordEvent
	{
		@TargetAggregateIdentifier
		public final String               id;
		public final CallUpRecordCmd.Save cmd;
	}

	/**
	 * 回调
	 */
	@Builder
	final class Callbacked implements CallUpRecordEvent
	{
		@TargetAggregateIdentifier
		public final String                   id;
		public final CallUpRecordCmd.Callback cmd;
	}
}
