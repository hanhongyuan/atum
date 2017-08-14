package com.oasis.atum.commons.domain.cmd;

import com.oasis.atum.commons.domain.enums.CallState;
import com.oasis.atum.commons.domain.enums.CallType;
import lombok.Builder;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.util.Date;

/**
 * 通话记录命令集
 */
public interface CallUpRecordCmd
{
	/**
	 * 创建
	 */
	@Builder
	final class Create implements CallUpRecordCmd
	{
		@TargetAggregateIdentifier
		public final String id;
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
	final class Update implements CallUpRecordCmd
	{
		@TargetAggregateIdentifier
		public final String    id;
		public final CallType  callType;
		public final Date      ringTime;
		public final Date      beginTime;
		public final Date      endTime;
		public final CallState callState;
		public final String    recordFile;
		public final String    fileServer;
		public final Boolean   isSuccess;
		public final String    message;
	}

	/**
	 * 回调
	 */
	@Builder
	final class Callback implements CallUpRecordCmd
	{
		@TargetAggregateIdentifier
		public final String  id;
		public final Boolean isSuccess;
		public final String  message;
	}
}
