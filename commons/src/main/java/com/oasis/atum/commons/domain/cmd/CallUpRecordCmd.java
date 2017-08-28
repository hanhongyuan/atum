package com.oasis.atum.commons.domain.cmd;

import com.oasis.atum.commons.infrastructure.enums.CallEventState;
import com.oasis.atum.commons.infrastructure.enums.CallState;
import com.oasis.atum.commons.infrastructure.enums.CallType;
import lombok.Builder;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.util.Date;

/**
 * 通话记录命令集
 */
public interface CallUpRecordCmd
{
	/**
	 * 绑定
	 */
	@Builder
	final class Bind
	{
		@TargetAggregateIdentifier
		public final String id;
		public final String thirdId;
		public final String callMobile;
		public final String callToMobile;
		public final Long   maxCallTime;
		public final String noticeUri;
		public final Date   createTime;
	}

	/**
	 * 修改
	 */
	@Builder
	final class Update
	{
		@TargetAggregateIdentifier
		public final String         id;
		public final String         callMobile;
		public final String         callToMobile;
		public final CallType       callType;
		public final Date           ringTime;
		public final Date           beginTime;
		public final Date           endTime;
		public final CallState      callState;
		public final CallEventState state;
		public final String         recordFile;
		public final String         fileServer;
		public final Boolean        isSuccess;
		public final String         message;
		public final String         callId;
	}

	/**
	 * 回调
	 */
	@Builder
	final class Callback
	{
		@TargetAggregateIdentifier
		public final String  id;
		public final Boolean isSuccess;
		public final String  message;
	}

	@Builder
	final class HangUp
	{
		@TargetAggregateIdentifier
		public final String id;
	}
}
