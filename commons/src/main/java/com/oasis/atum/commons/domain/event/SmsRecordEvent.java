package com.oasis.atum.commons.domain.event;

import com.oasis.atum.commons.domain.cmd.SmsRecordCmd;
import lombok.Builder;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

/**
 * 短信记录事件集
 */
public interface SmsRecordEvent
{
	/**
	 * 创建
	 */
	@Builder
	final class Created
	{
		@TargetAggregateIdentifier
		public final String              id;
		public final SmsRecordCmd.Create cmd;
	}

	/**
	 * 发送成功
	 */
	@Builder
	final class Succeed
	{
		@TargetAggregateIdentifier
		public final String               id;
		public final SmsRecordCmd.Success cmd;
	}

	/**
	 * 发送失败
	 */
	@Builder
	final class Failed
	{
		@TargetAggregateIdentifier
		public final String            id;
		public final SmsRecordCmd.Fail cmd;
	}

	/**
	 * 回复
	 */
	@Builder
	final class Replied
	{
		@TargetAggregateIdentifier
		public final String             id;
		public final SmsRecordCmd.Reply cmd;
	}
}
