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
	 * 绑定
	 */
	@Builder
	final class Bound
	{
		@TargetAggregateIdentifier
		public final String               id;
		public final CallUpRecordCmd.Bind cmd;

	}

	/**
	 * 修改
	 */
	@Builder
	final class Updated
	{
		@TargetAggregateIdentifier
		public final String                 id;
		public final CallUpRecordCmd.Update cmd;
	}

	/**
	 * 回调
	 */
	@Builder
	final class Callbacked
	{
		@TargetAggregateIdentifier
		public final String                   id;
		public final CallUpRecordCmd.Callback cmd;
	}

	/**
	 * 挂断
	 */
	@Builder
	final class HungUp
	{
		@TargetAggregateIdentifier
		public final String                 id;
		public final CallUpRecordCmd.HangUp cmd;
	}
}
