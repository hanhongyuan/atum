package com.oasis.atum.commons.domain.cmd;

import com.oasis.atum.commons.infrastructure.enums.SmsType;
import lombok.Builder;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.util.Date;
import java.util.List;

/**
 * 短信记录命令集
 */
public interface SmsRecordCmd
{
	/**
	 * 创建
	 */
	@Builder
	final class Create
	{
		@TargetAggregateIdentifier
		public final String  id;
		public final String  messageId;
		public final Integer count;
		public final String  mobile;
		public final String  content;
		public final SmsType smsType;
		public final Boolean isBusiness;
		public final Date    createTime;
	}

	/**
	 * 发送成功
	 */
	@Builder
	final class Success
	{
		@TargetAggregateIdentifier
		public final String  id;
		public final Boolean isSuccess;
		public final Integer count;
		public final Date    receiveTime;
		public final String  messageId;
	}

	/**
	 * 发送失败
	 */
	@Builder
	final class Fail
	{
		@TargetAggregateIdentifier
		public final String  id;
		public final Boolean isSuccess;
		public final String  errCode;
		public final String  messageId;
	}

	/**
	 * 回复
	 */
	@Builder
	final class Reply
	{
		@TargetAggregateIdentifier
		public final String  id;
		public final Boolean isSuccess;
		public final String  sender;
		public final String  content;
		public final Date    receiveTime;
		public final String  extendCode;
	}
}
